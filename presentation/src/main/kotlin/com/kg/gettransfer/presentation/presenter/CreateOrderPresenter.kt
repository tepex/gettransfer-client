package com.kg.gettransfer.presentation.presenter

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.R

import com.kg.gettransfer.core.domain.CityPoint
import com.kg.gettransfer.core.presentation.WorkerManager

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.interactor.PromoInteractor
import com.kg.gettransfer.domain.model.Dest
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.RouteInfoRequest
import com.kg.gettransfer.domain.model.TransferNew
import com.kg.gettransfer.domain.model.Trip
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.TransportType
import com.kg.gettransfer.domain.model.TransportTypePrice
import com.kg.gettransfer.domain.model.RouteInfo

import com.kg.gettransfer.extensions.isNonZero
import com.kg.gettransfer.extensions.simpleFormat

import com.kg.gettransfer.presentation.delegate.DateTimeDelegate
import com.kg.gettransfer.presentation.delegate.PassengersDelegate
import com.kg.gettransfer.presentation.mapper.UserMapper

import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.model.TransportTypeModel
import com.kg.gettransfer.presentation.model.map

import com.kg.gettransfer.presentation.ui.Utils

import com.kg.gettransfer.presentation.view.CreateOrderView
import com.kg.gettransfer.presentation.view.CreateOrderView.Companion.MAX_OFFERED_PRICE
import com.kg.gettransfer.presentation.view.CreateOrderView.Companion.MIN_OFFERED_PRICE
import com.kg.gettransfer.presentation.view.CreateOrderView.FieldError
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.sys.domain.GetPreferencesInteractor

import com.kg.gettransfer.utilities.Analytics
import com.kg.gettransfer.utilities.NewTransferState

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import moxy.InjectViewState

import org.koin.core.inject
import org.koin.core.parameter.parametersOf

@InjectViewState
@Suppress("TooManyFunctions")
class CreateOrderPresenter : BasePresenter<CreateOrderView>() {

    private val worker: WorkerManager by inject { parametersOf("CreateOrderPresenter") }
    private val promoInteractor: PromoInteractor by inject()
    private val dateDelegate: DateTimeDelegate by inject()
    private val childSeatsDelegate: PassengersDelegate by inject()

    private val userMapper: UserMapper by inject()

    private val nState: NewTransferState by inject()
    private val getPreferences: GetPreferencesInteractor by inject()

    private var currencies: List<CurrencyModel>? = null
    private var transportTypes: List<TransportTypeModel>? = null
    private var routeModel: RouteModel? = null
    private var polyline: PolylineModel? = null
    private var track: CameraUpdate? = null
    private var selectedCurrency = INVALID_CURRENCY_INDEX
    private var hintsToComments: List<String>? = null

    private var isMapInitialized = false
    private var isTimeSetByUser = false

    fun init() {
        updateRouteInfo(true)
        worker.main.launch {
            currencies = configsManager.getConfigs().supportedCurrencies.map { it.map() }
            setCurrency(sessionInteractor.currency.map())
        }
        with(accountManager) {
            if (isLoggedIn && remoteAccount.isBusinessAccount) {
                remoteAccount.partner?.defaultPromoCode?.let { promoCode ->
                    orderInteractor.promoCode = promoCode
                    viewState.disablePromoCodeField()
                }
            }
            viewState.setUser(userMapper.toView(tempUser), isLoggedIn)
        }
        with(orderInteractor) {
            viewState.setPassengers(passengers)
            viewState.setEditableFields(offeredPrice, flightNumber, flightNumberReturn, promoCode)
            if (promoCode.isNotEmpty()) checkPromoCode()
            viewState.setHourlyDuration(hourlyDuration)
        }
        updateChildSeatsInfo()
        checkOrderDateTime()
    }

    private fun updateRouteInfo(
        updateMap: Boolean,
        isDateOrDistanceChanged: Boolean = false
    ) {
        val from = orderInteractor.from?.cityPoint
        val to = orderInteractor.to?.cityPoint
        val hourlyDuration = orderInteractor.hourlyDuration
        if (!pointsAndDurationIsValid(from, to, hourlyDuration)) return
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            from?.let { from ->
                val routeInfo = getRouteInfo(from, to, hourlyDuration)

                hintsToComments = routeInfo.hintsToComments
                setTransportTypePrices(routeInfo.prices)

                to?.let { to ->
                    setPointToPointRoute(from, to, routeInfo, updateMap, isDateOrDistanceChanged)
                } ?: if (isMapInitialized && updateMap) setHourlyPoint(isDateOrDistanceChanged)
            }
            viewState.blockInterface(false)
        }
    }

    private fun pointsAndDurationIsValid(from: CityPoint?, to: CityPoint?, hourlyDuration: Int?): Boolean {
        val errorText =
            if (from?.point == null) {
                "`From` ($from) is not set"
            } else if (to?.point == null && hourlyDuration == null) {
                "`To` ($to) or 'Hourly duration' ($hourlyDuration) is not set"
            } else null
        errorText?.let { viewState.setError(ApiException(ApiException.APP_ERROR, it, false)) }
        return errorText == null
    }

    @Suppress("UnsafeCallOnNullableType")
    private suspend fun getRouteInfo(from: CityPoint, to: CityPoint?, hourlyDuration: Int?) =
        utils.asyncAwait {
            orderInteractor.getRouteInfo(
                RouteInfoRequest(
                    from.point!!,
                    to?.point,
                    hourlyDuration,
                    true,
                    if (hourlyDuration != null) false else dateDelegate.returnDate != null,
                    sessionInteractor.currency.code,
                    orderInteractor.orderStartTime,
                    orderInteractor.orderReturnTime
                )
            )
        }.model

    fun mapInitialized() {
        isMapInitialized = true
        with(orderInteractor) {
            if (from == null || to == null && hourlyDuration == null) {
                return
            } else if (to == null) {
                setHourlyPoint()
                return
            } else {
                if (routeModel != null) setRoute()
            }
        }
    }

    private fun setPointToPointRoute(
        from: CityPoint,
        to: CityPoint,
        routeInfo: RouteInfo,
        updateMap: Boolean,
        isDateOrDistanceChanged: Boolean = false
    ) {
        from.point?.let { fromPoint ->
            to.point?.let { toPoint ->
                val isRoundTrip = dateDelegate.returnDate != null
                routeModel = RouteModel(
                    from.name,
                    to.name,
                    fromPoint,
                    toPoint,
                    dateDelegate.run { startOrderedTime ?: getCurrentDate().time.simpleFormat() },
                    routeInfo.distance?.let { if (isRoundTrip) it.times(2) else it },
                    isRoundTrip,
                    routeInfo.polyLines
                )

                if (isMapInitialized && updateMap) setRoute(isDateOrDistanceChanged)
            }
        }
    }

    private fun setHourlyPoint(isDateChanged: Boolean = false) {
        orderInteractor.from?.let { from ->
            from.cityPoint.point?.let { p ->
                val point = LatLng(p.latitude, p.longitude)
                Utils.getCameraUpdateForPin(point)?.let { cameraUpdate ->
                    track = cameraUpdate
                    viewState.setPinHourlyTransfer(
                        from.address ?: "",
                        dateDelegate.run { startOrderedTime ?: getCurrentDate().time.simpleFormat() },
                        point,
                        cameraUpdate,
                        isDateChanged
                    )
                }
            }
        }
    }

    private fun setRoute(isDateOrDistanceChanged: Boolean = false) {
        utils.launchSuspend {
            @Suppress("UnsafeCallOnNullableType")
            routeModel?.let { routeModel ->
                if (polyline == null) {
                    utils.compute {
                        polyline = Utils.getPolyline(routeModel)
                        track = polyline!!.track
                    }
                }
                viewState.setRoute(polyline!!, routeModel, isDateOrDistanceChanged)
            }
        }
    }

    private fun checkOrderDateTime() = with(orderInteractor) {
        orderStartTime?.let { time ->
            viewState.setDateTimeTransfer(time.simpleFormat(), true)
            isTimeSetByUser = true
        } ?: viewState.setHintForDateTimeTransfer(orderInteractor.hourlyDuration == null)
        if (hourlyDuration != null) {
            viewState.setTripType(false)
        } else {
            orderReturnTime?.let { viewState.setDateTimeTransfer(it.simpleFormat(), false) }
        }
    }

    fun showHourlyDurationDialog() {
        viewState.showHourlyDurationDialog(orderInteractor.hourlyDuration)
    }

    fun updateDuration(durationValue: Int) {
        orderInteractor.apply {
            updateRouteInfo(false)
            hourlyDuration = durationValue
            viewState.setHourlyDuration(hourlyDuration)
        }
    }

    private suspend fun setTransportTypePrices(prices: Map<TransportType.ID, TransportTypePrice>) {
        utils.compute {
            val pr = prices.mapValues { it.value.map() }
            val newTransportTypes = configsManager.getConfigs().transportTypes.map { it.map(pr) }
            transportTypes?.let { tt ->
                newTransportTypes.forEach { it.checked = tt.find { old -> old.id == it.id }?.checked ?: false }
            }
            transportTypes = newTransportTypes
        }

        orderInteractor.selectedTransports?.let {
            setSelectedTransportTypes()
        } ?: setFavoriteTransportTypes()

        transportTypes?.let { viewState.setTransportTypes(it) }
    }

    fun changeDate(isStartDate: Boolean) {
        if (isStartDate) isTimeSetByUser = true
        updateRouteInfo(true, isDateOrDistanceChanged = true)
    }

    fun clearReturnDate() {
        dateDelegate.returnDate = null
        orderInteractor.flightNumberReturn = null
        updateRouteInfo(true, isDateOrDistanceChanged = true)
    }

    fun currencyChanged(currency: CurrencyModel) {
        setCurrency(currency, true)
        updateRouteInfo(false)
    }

    private fun setCurrency(currency: CurrencyModel, hideCurrencies: Boolean = false) {
        viewState.setCurrency(currency.symbol, hideCurrencies)
        currencies?.indexOf(currency)?.let { selectedCurrency = it }
    }

    fun changePassengers(count: Int) {
        with(orderInteractor) {
            if (isAutoChangePassengers) isAutoChangePassengers = false
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
            if (returnWay) orderInteractor.flightNumberReturn = it else orderInteractor.flightNumber = it
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
            if (result.error == null) {
                viewState.setPromoResult(result.model.discount)
            } else {
                viewState.setPromoResult(null)
            }
            viewState.blockInterface(false)
        }
    }

    fun setAgreeLicence(agreeLicence: Boolean) {
        accountManager.tempUser.termsAccepted = agreeLicence
    }

    fun showLicenceAgreement() = router.navigateTo(Screens.LicenceAgree)

    fun onGetTransferClick() {
        if (!checkFieldsForRequest()) return

        val transferNew = getTransfer()

        transferNew?.let {
            utils.launchSuspend {
                viewState.blockInterface(true, true)
                val result = utils.asyncAwait { transferInteractor.createTransfer(transferNew) }
                if (result.error == null) {
                    putAccount(result)
                } else {
                    handleErrorCreateTransfer(result)
                }
                viewState.blockInterface(false)
            }
        } ?: viewState.setError(false, R.string.LNG_RIDE_CANT_CREATE)
    }

    private fun handleErrorCreateTransfer(result: Result<Transfer>) {
        logCreateTransfer(Analytics.SERVER_ERROR)
        result.error?.let { error ->
            when {
                error.isPhoneTaken() -> router.newRootScreen(
                    Screens.MainLogin(Screens.CLOSE_AFTER_LOGIN, accountManager.tempProfile.phone)
                )
                error.isEarlyDateError() -> viewState.setError(false, R.string.LNG_DATE_EARLY_ERROR)
                error.isLateDateError() -> viewState.setError(false, R.string.LNG_DATE_LATE_ERROR)
                error.code == ApiException.NETWORK_ERROR -> viewState.setError(
                    false,
                    R.string.LNG_NETWORK_ERROR
                )
                else -> viewState.setError(error)
            }
        }
    }

    private suspend fun putAccount(result: Result<Transfer>) {
        val logResult = utils.asyncAwait { accountManager.putAccount(connectSocket = true) }
        logResult.error?.let { error ->
            if (error.isNotLoggedIn() || error.isAccountExistError()) {
                onAccountExists(result.model.id)
            }
        } ?: run {
            utils.compute { handleSuccess() }
            router.replaceScreen(Screens.Offers(result.model.id))
        }
    }

    @Suppress("ComplexMethod")
    private fun getTransfer(): TransferNew? {
        val hourlyDuration = orderInteractor.hourlyDuration
        val toPoint: Dest<CityPoint, Int>? = orderInteractor.to?.let { to ->
            hourlyDuration?.let { hourly -> Dest.Duration(hourly, to.cityPoint) } ?: Dest.Point(to.cityPoint)
        } ?: hourlyDuration?.let { Dest.Duration(it, null) }

        return toPoint?.let { dest ->
            orderInteractor.from?.cityPoint?.let { from ->
                transportTypes?.filter { it.checked }?.map { it.id }?.let { transportTypes ->
                    TransferNew(
                        from,
                        dest,
                        Trip(dateDelegate.startDate, orderInteractor.flightNumber),
                        dateDelegate.returnDate?.let { Trip(it, orderInteractor.flightNumberReturn) },
                        transportTypes,
                        orderInteractor.passengers + childSeatsDelegate.getTotalSeats(),
                        childSeatsDelegate.infantSeats.isNonZero(),
                        childSeatsDelegate.convertibleSeats.isNonZero(),
                        childSeatsDelegate.boosterSeats.isNonZero(),
                        orderInteractor.offeredPrice?.times(CENTS)?.toInt(),
                        orderInteractor.comment,
                        orderInteractor.nameSign,
                        accountManager.tempUser,
                        orderInteractor.promoCode,
                        false)
                }
            }
        }
    }

    private fun onAccountExists(transferId: Long) {
        accountManager.clearRemoteUser()
        viewState.showNotLoggedAlert(transferId)
    }

    private fun handleSuccess() {
        logGetOffers()
        saveChosenTransportTypes(getSelectedTransportTypes())
        releaseDelegates()
    }

    private fun checkFieldsForRequest(): Boolean {
        return getErrorField()?.let { errorField ->
            errorField.value?.let { logCreateTransfer(it) }
            viewState.showEmptyFieldError(errorField.stringId, errorField.formatArg)
            viewState.highLightErrorField(errorField)
            false
        } ?: true
    }

    private fun getErrorField() =
        when {
            !isTimeSetByUser                            -> FieldError.TIME_NOT_SELECTED
            !dateDelegate.validate()                    -> FieldError.RETURN_TIME
            transportTypes?.none { it.checked } == true -> FieldError.TRANSPORT_FIELD
            else -> isValidOfferedPrice() ?: accountManager.isValidProfileForCreateOrder()
        }

    private fun isValidOfferedPrice() =
        orderInteractor.offeredPrice?.let { offeredPrice ->
            when {
                offeredPrice < MIN_OFFERED_PRICE -> FieldError.OFFERED_PRICE_MIN
                offeredPrice > MAX_OFFERED_PRICE -> FieldError.OFFERED_PRICE_MAX
                else -> null
            }
        }

    fun onSelectedTransportTypesChanged() {
        saveSelectedTransportTypes()
        setPassengersCountForSelectedTransportTypes()
    }

    private fun setPassengersCountForSelectedTransportTypes(setSavedPax: Boolean = false) {
        if (setSavedPax) {
            viewState.setPassengers(orderInteractor.passengers)
            return
        }
        if (orderInteractor.isAutoChangePassengers) {
            transportTypes?.let { list ->
                val hasBigTypes = hasTransportType(list, TransportType.BIG_TRANSPORT)
                val hasBusTypes = hasTransportType(list, TransportType.BUS_TRANSPORT)
                val pax = when {
                    hasBigTypes -> DEFAULT_BIG_TRANSPORT_PASSENGER_COUNT
                    hasBusTypes -> DEFAULT_BUS_PASSENGER_COUNT
                    else -> DEFAULT_SMALL_TRANSPORT_PASSENGER_COUNT
                }
                orderInteractor.passengers = pax
                viewState.setPassengers(pax)
            }
        }
    }

    /**
     * Checks if list of [transportTypes] contains one of [specificTypes] of transport
     */
    private fun hasTransportType(transportTypes: List<TransportTypeModel>, specificTypes: List<TransportType.ID>) =
        transportTypes.filter { it.checked }.map { it.id }.any { specificTypes.contains(it) }

    fun onCenterRouteClick() {
        track?.let { viewState.centerRoute(it) }
        analytics.logSingleEvent(Analytics.SHOW_ROUTE_CLICKED)
    }

    private suspend fun setFavoriteTransportTypes() {
        val favoriteTransports = withContext(worker.bg) { getPreferences().getModel() }.favoriteTransports
        if (favoriteTransports.isNotEmpty()) {
            selectTransportTypes(favoriteTransports)
            setPassengersCountForSelectedTransportTypes()
        }
    }

    private fun setSelectedTransportTypes() =
        orderInteractor.selectedTransports?.let { selected ->
            selectTransportTypes(selected)
            setPassengersCountForSelectedTransportTypes(true)
        }

    private fun selectTransportTypes(selectedTransport: Set<TransportType.ID>) {
        selectedTransport.forEach { favorite ->
            transportTypes?.find { it.id == favorite }?.checked = true
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

    // Analytics

    fun logTransferSettingsEvent(value: String) =
        analytics.logEvent(Analytics.EVENT_TRANSFER_SETTINGS, Analytics.PARAM_KEY_FIELD, value)

    private fun logCreateTransfer(result: String) {

        val currency = if (selectedCurrency != INVALID_CURRENCY_INDEX) currencies?.get(selectedCurrency)?.name else null

        analytics.logCreateTransfer(
            result,
            orderInteractor.offeredPrice?.toString(),
            currency,
            orderInteractor.hourlyDuration ?: orderInteractor.duration
        )
    }

    private fun logEventAddToCart() {

        val tripType = when {
            orderInteractor.hourlyDuration != null -> Analytics.TRIP_HOURLY
            dateDelegate.returnDate != null        -> Analytics.TRIP_ROUND
            else                                   -> Analytics.TRIP_DESTINATION
        }

        val value = orderInteractor.offeredPrice?.toString()

        val currency = if (selectedCurrency != INVALID_CURRENCY_INDEX) currencies?.get(selectedCurrency)?.name else null

        analytics.logEventAddToCart(
            transportTypes?.filter { it.checked }?.joinToString(),
            orderInteractor.hourlyDuration ?: orderInteractor.duration,
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
        if (!isBusinessAccount()) {
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
        private const val DEFAULT_BUS_PASSENGER_COUNT = 8
        private const val CENTS = 100
    }
}
