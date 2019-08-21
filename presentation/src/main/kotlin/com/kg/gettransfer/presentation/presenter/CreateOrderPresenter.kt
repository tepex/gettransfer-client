package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.R

import com.kg.gettransfer.core.presentation.WorkerManager

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.interactor.OrderInteractor
import com.kg.gettransfer.domain.interactor.PromoInteractor
import com.kg.gettransfer.domain.model.RouteInfoHourlyRequest
import com.kg.gettransfer.domain.model.RouteInfoRequest
import com.kg.gettransfer.domain.model.TransferNew
import com.kg.gettransfer.domain.model.DestDuration
import com.kg.gettransfer.domain.model.DestPoint
import com.kg.gettransfer.domain.model.Trip
import com.kg.gettransfer.domain.model.RouteInfo
import com.kg.gettransfer.domain.model.TransportType
import com.kg.gettransfer.domain.model.TransportTypePrice

import com.kg.gettransfer.extensions.isNonZero
import com.kg.gettransfer.extensions.simpleFormat

import com.kg.gettransfer.presentation.delegate.DateTimeDelegate
import com.kg.gettransfer.presentation.delegate.PassengersDelegate
import com.kg.gettransfer.presentation.mapper.RouteMapper
import com.kg.gettransfer.presentation.mapper.UserMapper

import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.model.TransportTypeModel
import com.kg.gettransfer.presentation.model.map

import com.kg.gettransfer.presentation.ui.Utils

import com.kg.gettransfer.presentation.view.CreateOrderView
import com.kg.gettransfer.presentation.view.CreateOrderView.FieldError
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.sys.domain.SetFavoriteTransportsInteractor
import com.kg.gettransfer.sys.presentation.ConfigsManager

import com.kg.gettransfer.utilities.Analytics
import com.kg.gettransfer.utilities.NewTransferState

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import org.koin.core.inject
import org.koin.core.parameter.parametersOf

@InjectViewState
class CreateOrderPresenter : BasePresenter<CreateOrderView>() {

    private val worker: WorkerManager by inject { parametersOf("CreateOrderPresenter") }
    private val orderInteractor: OrderInteractor by inject()
    private val promoInteractor: PromoInteractor by inject()
    private val dateDelegate: DateTimeDelegate by inject()
    private val childSeatsDelegate: PassengersDelegate by inject()
    private val configsManager: ConfigsManager by inject()

    private val routeMapper: RouteMapper by inject()
    private val userMapper: UserMapper by inject()

    private val nState: NewTransferState by inject()
    private val setFavoriteTransports: SetFavoriteTransportsInteractor by inject()

    private val currencies by lazy { configsManager.configs.supportedCurrencies.map { it.map() } }
    private var duration: Int? = null
    private var transportTypes: List<TransportTypeModel>? = null
    private var routeModel: RouteModel? = null
    private var polyline: PolylineModel? = null
    private var track: CameraUpdate? = null
    private var selectedCurrency = INVALID_CURRENCY_INDEX
    private var hintsToComments: List<String>? = null

    private var isMapInitialized = false
    private var isTimeSetByUser = false

    fun init() {
        initMapAndPrices()
        setCurrency(sessionInteractor.currency.map())
        with(orderInteractor) {
            viewState.setUser(userMapper.toView(accountManager.tempUser), accountManager.isLoggedIn)
            viewState.setPassengers(passengers)
            viewState.setEditableFields(offeredPrice, flightNumber, flightNumberReturn, promoCode)
            if (promoCode.isNotEmpty()) checkPromoCode()
        }
        updateChildSeatsInfo()
        checkOrderDateTime()
    }

    private fun checkOrderDateTime() = with(orderInteractor) {
        orderStartTime?.let {
            viewState.setDateTimeTransfer(it.simpleFormat(), true)
            isTimeSetByUser = true
        } ?: viewState.setHintForDateTimeTransfer(orderInteractor.hourlyDuration == null)
        if (hourlyDuration != null) {
            viewState.setTripType(false)
        } else {
            orderReturnTime?.let { viewState.setDateTimeTransfer(it.simpleFormat(), false) }
        }
    }

    private fun initMapAndPrices() {
        with(orderInteractor) {
            if (from == null || (to == null && hourlyDuration == null)) {
//                Timber.w("routerInteractor init error. from: $from, to: $to, duration: $hourlyDuration")
            } else if (hourlyDuration != null) { // not need route info when hourly
                getPricesForHourlyTransfer(hourlyDuration!!)
            } else {
                getRouteAndPricesForPointToPointTransfer()
            }
        }
    }

    private fun getPricesForHourlyTransfer(duration: Int) {
        val from = orderInteractor.from?.cityPoint
        val dateTime = orderInteractor.orderStartTime
        if (from?.point == null) {
//            Timber.w("NPE! from: $from")
            viewState.setError(ApiException(ApiException.APP_ERROR, "`From` ($from) is not set"))
            return
        }
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val prices = utils.asyncAwait {
                    orderInteractor.getRouteInfoHourlyTransfer(
                            RouteInfoHourlyRequest(
                                    from.point!!,
                                    duration,
                                    sessionInteractor.currency.code,
                                    dateTime
                            )
                    )}.model.prices
            setTransportTypePrices(prices)
            viewState.blockInterface(false)
        }
    }

    private fun getRouteAndPricesForPointToPointTransfer() {
        val from = orderInteractor.from!!.cityPoint
        val to = orderInteractor.to!!.cityPoint
        val dateTime = orderInteractor.orderStartTime
        if (from.point == null || to.point == null) {
//            Timber.w("NPE! from: $from, to: $to")
            viewState.setError(ApiException(ApiException.APP_ERROR, "`From` ($from) or `To` {$to} is not set"))
            return
        }
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val route: RouteInfo? = utils.asyncAwait {
                orderInteractor.getRouteInfo(
                        RouteInfoRequest(
                                from.point!!,
                                to.point!!,
                                true,
                                false,
                                sessionInteractor.currency.code,
                                dateTime
                        )
                )
            }.model
            route?.let {
                duration = it.duration
                hintsToComments = it.hintsToComments
            }
            setTransportTypePrices(route?.prices ?: emptyMap())

            routeModel = routeMapper.getView(
                    route?.distance,
                    route?.polyLines,
                    from.name,
                    to.name,
                    from.point!!,
                    to.point!!,
                    dateDelegate.run {
                        startOrderedTime ?: getCurrentDatePlusMinimumHours().time.simpleFormat()
                    }
            )

            if (isMapInitialized) setRoute()
            viewState.blockInterface(false)
        }
    }

    fun mapInitialized() {
        isMapInitialized = true
        with(orderInteractor) {
            if (from == null || (to == null && hourlyDuration == null)) {
//                Timber.w("routerInteractor init error. from: $from, to: $to, duration: $hourlyDuration")
                return
            } else if (hourlyDuration != null) { // not need route info when hourly
                setHourlyPoint()
                return
            } else {
                if (routeModel != null) setRoute()
            }
        }
    }

    private fun setHourlyPoint(isDateChanged: Boolean = false) {
        orderInteractor.from?.let { from ->
            from.cityPoint.point?.let { p ->
                val point = LatLng(p.latitude, p.longitude)
                track = Utils.getCameraUpdateForPin(point)
                viewState.setPinHourlyTransfer(
                    from.address ?: "",
                    dateDelegate.run { startOrderedTime ?: getCurrentDatePlusMinimumHours().time.simpleFormat() },
                    point,
                    track!!,
                    isDateChanged)
            }
        }
    }

    private fun setRoute() {
        utils.launchSuspend {
            routeModel?.let {
                utils.compute {
                    polyline = Utils.getPolyline(it)
                    track = polyline!!.track
                }
                viewState.setRoute(polyline!!, it, false)
            }
        }
    }

    private fun getNewPrices() {
        if (orderInteractor.hourlyDuration != null) {
            initPrices(orderInteractor.hourlyDuration!!)
        } else {
            initPrices(dateDelegate.returnDate != null)
        }
    }

    private fun initPrices(returnWay: Boolean) {
        val from = orderInteractor.from?.cityPoint
        val to = orderInteractor.to?.cityPoint
        val dateTime = orderInteractor.orderStartTime
        if (from?.point == null || to?.point == null) {
//            Timber.w("NPE! from: $from, to: $to")
            viewState.setError(ApiException(ApiException.APP_ERROR, "`From` ($from) or `To` {$to} is not set"))
            return
        }
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val prices = utils.asyncAwait {
                orderInteractor.getRouteInfo(
                    RouteInfoRequest(
                        from.point!!,
                        to.point!!,
                        true,
                        returnWay,
                        sessionInteractor.currency.code,
                        dateTime
                    )
                )
            }.model.prices
            setTransportTypePrices(prices)
            viewState.blockInterface(false)
        }
    }

    private fun initPrices(duration: Int) {
        val from = orderInteractor.from?.cityPoint
        val dateTime = orderInteractor.orderStartTime
        if (from?.point == null) {
//            Timber.w("NPE! from: $from")
            viewState.setError(ApiException(ApiException.APP_ERROR, "`From` ($from) is not set"))
            return
        }
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val prices = utils.asyncAwait {
                orderInteractor.getRouteInfoHourlyTransfer(
                    RouteInfoHourlyRequest(
                        from.point!!,
                        duration,
                        sessionInteractor.currency.code,
                        dateTime
                    )
                )
            }.model.prices
            setTransportTypePrices(prices)
            viewState.blockInterface(false)
        }
    }

    private suspend fun setTransportTypePrices(
        prices: Map<TransportType.ID, TransportTypePrice>,
        selectTransport: Boolean = false
    ) {
        utils.compute {
            val pr = prices.mapValues { it.value.map() }
            val newTransportTypes = configsManager.configs.transportTypes.map { it.map(pr) }
            transportTypes?.let { tt ->
                newTransportTypes.forEach { it.checked = tt.find { old -> old.id == it.id }?.checked ?: false }
            }
            transportTypes = newTransportTypes
        }
        if (selectTransport)
            if (orderInteractor.selectedTransports != null) {
                setSelectedTransportTypes()
            } else {
                setFavoriteTransportTypes()
            }
        viewState.setTransportTypes(transportTypes!!)
    }

    fun changeDate(isStartDate: Boolean) {
        if (isStartDate) {
            isTimeSetByUser = true
            if (orderInteractor.hourlyDuration != null) {
                setHourlyPoint(true)
            } else {
                routeModel?.let {
                    it.dateTime = dateDelegate.startOrderedTime!!  //value came from DateDelegate, so start_time was set
                    viewState.setRoute(polyline!!, it, true)
                }
            }
            getNewPrices()
        }
    }

    fun clearReturnDate() {
        dateDelegate.returnDate = null
        orderInteractor.flightNumberReturn = null
        initPrices(false)
    }

    fun currencyChanged(currency: CurrencyModel) {
        setCurrency(currency, true)
        saveChangedSettings()
        getNewPrices()
    }

    private fun saveChangedSettings() {
        utils.launchSuspend {
            viewState.blockInterface(true)
            val result = utils.asyncAwait { accountManager.saveSettings() }
            result.error?.let { if (!it.isNotLoggedIn()) viewState.setError(it) }
            viewState.blockInterface(false)
        }
    }

    private fun setCurrency(currency: CurrencyModel, hideCurrencies: Boolean = false) {
        viewState.setCurrency(currency.symbol, hideCurrencies)
        selectedCurrency = currencies.indexOf(currency)
    }

    fun changePassengers(count: Int) {
        with(orderInteractor) {
            passengers += count
            if (passengers < MIN_PASSENGERS) passengers = MIN_PASSENGERS
            viewState.setPassengers(passengers)
        }
        logTransferSettingsEvent(Analytics.PASSENGERS_ADDED)
    }

    fun updateChildSeatsInfo() {
        with(childSeatsDelegate) {
            viewState.setChildSeats(getDescription(), getTotalSeats())
        }
    }

    fun setOfferedPrice(price: Double?) {
        orderInteractor.offeredPrice = price
    }

    fun setName(name: String) {
        orderInteractor.nameSign = name
    }

    fun setFlightNumber(flightNumber: String, returnWay: Boolean) {
        (if (flightNumber.isEmpty()) null else flightNumber).let {
            if (returnWay) orderInteractor.flightNumberReturn = it
            else orderInteractor.flightNumber = it
        }
        logTransferSettingsEvent(Analytics.FLIGHT_NUMBER_ADDED)
    }

    fun setPromo(codeText: String) {
        orderInteractor.promoCode = codeText
        if (codeText.isEmpty()) viewState.resetPromoView()
    }

    fun checkPromoCode() {
        if (orderInteractor.promoCode.isEmpty()) return
        utils.launchSuspend {
            viewState.blockInterface(true)
            val result = utils.asyncAwait { promoInteractor.getDiscountByPromo(orderInteractor.promoCode) }
            if (result.error == null) viewState.setPromoResult(result.model.discount) else viewState.setPromoResult(null)
            viewState.blockInterface(false)
        }
    }

    fun setAgreeLicence(agreeLicence: Boolean) {
        accountManager.tempUser.termsAccepted = agreeLicence
    }

    fun showLicenceAgreement() = router.navigateTo(Screens.LicenceAgree)

    fun onGetTransferClick() {
        if (!checkFieldsForRequest()) return

        val from = orderInteractor.from!!
        val to = orderInteractor.to
        /*
        val selectedTransportTypes = transportTypes!!.filter { it.checked }.map { it.id }
        var pax = orderInteractor.passengers + childSeatsDelegate.getTotalSeats()
        if (pax == 0) {
            pax = if (selectedTransportTypes.any { TransportType.BIG_TRANSPORT.indexOf(it) >= 0 } ) {
                DEFAULT_BIG_TRANSPORT_PASSENGER_COUNT
            } else {
                DEFAULT_SMALL_TRANSPORT_PASSENGER_COUNT
            }
        }
        */
        val transferNew = TransferNew(
            from.cityPoint,
            if (orderInteractor.hourlyDuration != null) DestDuration(orderInteractor.hourlyDuration!!) else DestPoint(to!!.cityPoint),
            Trip(dateDelegate.startDate, orderInteractor.flightNumber),
            dateDelegate.returnDate?.let { Trip(it, orderInteractor.flightNumberReturn) },
            transportTypes!!.filter { it.checked }.map { it.id },
            orderInteractor.passengers + childSeatsDelegate.getTotalSeats(),
            childSeatsDelegate.infantSeats.isNonZero(),
            childSeatsDelegate.convertibleSeats.isNonZero(),
            childSeatsDelegate.boosterSeats.isNonZero(),
            orderInteractor.offeredPrice?.times(100)?.toInt(),
            orderInteractor.comment,
            orderInteractor.nameSign,
            accountManager.tempUser,
            orderInteractor.promoCode,
            false
        )
//        Timber.d("new transfer: $transferNew")

        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val result = utils.asyncAwait { transferInteractor.createTransfer(transferNew) }
            if (result.error == null) {
                val logResult = utils.asyncAwait { accountManager.putAccount(connectSocket = true) }
                if (logResult.error == null) {
                    utils.compute {  handleSuccess() }
                    router.replaceScreen(Screens.Offers(result.model.id))
                } else if (logResult.error!!.isNotLoggedIn() || logResult.error!!.isAccountExistError()) {
                    onAccountExists(result.model.id)
                }
            } else {
                logCreateTransfer(Analytics.SERVER_ERROR)
                when {
                    result.error!!.isPhoneTaken() -> {
                        router.newRootScreen(
                            Screens.MainLogin(
                                Screens.CLOSE_AFTER_LOGIN,
                                accountManager.tempProfile.phone
                            )
                        )
                    }
                    result.error!!.isEarlyDateError() -> viewState.setError(false, R.string.LNG_DATE_EARLY_ERROR)
                    result.error!!.code == ApiException.NETWORK_ERROR -> viewState.setError(
                        false,
                        R.string.LNG_NETWORK_ERROR
                    )
                    else -> viewState.setError(result.error!!)
                }
            }

            //403 - есть акк, но не выполнен вход
            //500 - нет акка

            viewState.blockInterface(false)
        }
    }

    private fun onAccountExists(transferId: Long) {
        accountManager.clearRemoteUser()
        viewState.showNotLoggedAlert(transferId)
    }

    private fun handleSuccess() {
        logGetOffers()
        saveChosenTransportTypes()
        releaseDelegates()
    }

    private fun checkFieldsForRequest(): Boolean {
        var errorField = when {
            !isTimeSetByUser -> FieldError.TIME_NOT_SELECTED
            !dateDelegate.validate() -> FieldError.RETURN_TIME
            transportTypes?.none { it.checked } == true -> FieldError.TRANSPORT_FIELD
            else -> null
        }
        if (errorField == null) errorField = accountManager.isValidProfileForCreateOrder()
        if (errorField == null) return true
        logCreateTransfer(errorField.value)
        viewState.showEmptyFieldError(errorField.stringId)
        viewState.highLightErrorField(errorField)
        return false
    }

    fun setPassengersCountForSelectedTransportTypes(setSavedPax: Boolean = false) {
        if (setSavedPax) {
            viewState.setPassengers(orderInteractor.passengers)
            return
        }
        transportTypes!!.filter { it.checked }.map { it.id }
            .any { TransportType.BIG_TRANSPORT.indexOf(it) >= 0 }
            .let {
                (if (it) DEFAULT_BIG_TRANSPORT_PASSENGER_COUNT
                else DEFAULT_SMALL_TRANSPORT_PASSENGER_COUNT).let { pax ->
                    orderInteractor.passengers = pax
                    viewState.setPassengers(pax)
                }
            }
    }

    fun onCenterRouteClick() {
        track?.let { viewState.centerRoute(it) }
        analytics.logSingleEvent(Analytics.SHOW_ROUTE_CLICKED)
    }

    private fun setFavoriteTransportTypes() = worker.main.launch {
        if (!getPreferences().getModel().favoriteTransports.isEmpty()) {
            selectTransportTypes(getPreferences().getModel().favoriteTransports)
            setPassengersCountForSelectedTransportTypes()
        }
    }

    private fun setSelectedTransportTypes() =
        orderInteractor.selectedTransports?.let {
            selectTransportTypes(it)
            setPassengersCountForSelectedTransportTypes(true)
        }

    private fun selectTransportTypes(selectedTransport: Set<TransportType.ID>) {
        selectedTransport.forEach { favorite ->
            transportTypes?.find { it.id == favorite }?.checked = true
        }
    }

    private fun saveChosenTransportTypes() {
        worker.main.launch {
            withContext(worker.bg) { setFavoriteTransports(getSelectedTransportTypes()) }
        }
    }

    private fun saveSelectedTransportTypes() {
        orderInteractor.selectedTransports = getSelectedTransportTypes()
    }

    private fun getSelectedTransportTypes() =
        transportTypes?.filter { it.checked }?.map { it.id }?.toSet() ?: emptySet()

    private fun releaseDelegates() {
        dateDelegate.resetAfterOrder()
        childSeatsDelegate.clearSeats()
        orderInteractor.clear()
        nState.switchToMain()
    }

    fun onBackClick() = onBackCommandClick()

    override fun onBackCommandClick() {
        saveSelectedTransportTypes()
        router.exit()
        analytics.logSingleEvent(Analytics.BACK_TO_MAP)
    }

    fun redirectToLogin(id: Long) {
        with(accountManager.tempProfile) {
            router.navigateTo(
                Screens.LoginToGetOffers(id, if (!phone.isNullOrEmpty()) phone else email)
            )
        }
    }

    /////////Analytics////////

    fun logTransferSettingsEvent(value: String) =
        analytics.logEvent(Analytics.EVENT_TRANSFER_SETTINGS, Analytics.PARAM_KEY_FIELD, value)

    private fun logCreateTransfer(result: String) {

        val currency = if (selectedCurrency != INVALID_CURRENCY_INDEX) currencies[selectedCurrency].name else null

        analytics.logCreateTransfer(
            result,
            orderInteractor.offeredPrice?.let { it.toString() },
            currency,
            duration?.let { it }
        )
    }

    private fun logEventAddToCart() {
        val tripType = when {
            duration != null                -> Analytics.TRIP_HOURLY
            dateDelegate.returnDate != null -> Analytics.TRIP_ROUND
            else                            -> Analytics.TRIP_DESTINATION
        }.let { it }

        val value = orderInteractor.offeredPrice?.let { it.toString() }

        val currency = if (selectedCurrency != INVALID_CURRENCY_INDEX) currencies[selectedCurrency].name else null

        analytics.logEventAddToCart(
            transportTypes?.filter { it.checked }?.joinToString(),
            duration?.let { it },
            tripType,
            value,
            currency
        )
    }

    private fun logStartScreenOrder() {
        val startScreenOrder = false
        val event = if (startScreenOrder) Analytics.FROM_FORM else Analytics.FROM_MAP
        analytics.logEvent(Analytics.EVENT_TRANSFER, Analytics.ORDER_CREATED_FROM, event)
    }

    private fun logGetOffers() {
        logCreateTransfer(Analytics.RESULT_SUCCESS)
        logEventAddToCart()
        logStartScreenOrder()
    }

    fun setComment(comment: String) {
        orderInteractor.comment = if (comment.isNotEmpty()) comment else null
    }

    fun commentClick(comment: String) {
        viewState.showCommentDialog(comment, hintsToComments)
    }

    fun onTransportTypeClicked() {
        analytics.logSingleEvent(Analytics.CAR_INFO_CLICKED)
    }

    fun onChangeCurrencyClick() {
        if (!accountManager.remoteAccount.isBusinessAccount) {
            viewState.showCurrencies()
        }
    }

    override fun onDestroy() {
        worker.cancel()
        super.onDestroy()
    }

    companion object {
        private const val INVALID_CURRENCY_INDEX = -1

        const val MIN_PASSENGERS = 1
        private const val DEFAULT_SMALL_TRANSPORT_PASSENGER_COUNT = 2
        private const val DEFAULT_BIG_TRANSPORT_PASSENGER_COUNT = 4
    }
}
