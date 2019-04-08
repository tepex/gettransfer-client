package com.kg.gettransfer.presentation.presenter

import android.os.Bundle
import android.support.annotation.CallSuper

import android.util.Patterns
import com.appsflyer.AFInAppEventParameterName
import com.appsflyer.AFInAppEventType

import com.arellomobile.mvp.InjectViewState
import com.facebook.appevents.AppEventsConstants

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.ApiException

import com.kg.gettransfer.domain.interactor.PromoInteractor
import com.kg.gettransfer.domain.interactor.OrderInteractor

import com.kg.gettransfer.domain.model.DestDuration
import com.kg.gettransfer.domain.model.DestPoint
import com.kg.gettransfer.domain.model.TransferNew
import com.kg.gettransfer.domain.model.Trip
import com.kg.gettransfer.domain.model.TransportType
import com.kg.gettransfer.domain.model.TransportTypePrice
import com.kg.gettransfer.domain.model.RouteInfo
import com.kg.gettransfer.extensions.simpleFormat
import com.kg.gettransfer.presentation.delegate.DateTimeDelegate

import com.kg.gettransfer.presentation.mapper.CurrencyMapper
import com.kg.gettransfer.presentation.mapper.RouteMapper
import com.kg.gettransfer.presentation.mapper.TransportTypeMapper
import com.kg.gettransfer.presentation.mapper.TransportTypePriceMapper
import com.kg.gettransfer.presentation.mapper.UserMapper

import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.model.TransportTypeModel
import com.kg.gettransfer.presentation.model.TripDate

import com.kg.gettransfer.presentation.ui.SystemUtils
import com.kg.gettransfer.presentation.ui.Utils

import com.kg.gettransfer.presentation.view.CreateOrderView
import com.kg.gettransfer.presentation.view.CreateOrderView.FieldError
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics

import java.util.Calendar
import java.util.Date

import org.koin.standalone.get
import org.koin.standalone.inject

import timber.log.Timber

@InjectViewState
class CreateOrderPresenter : BasePresenter<CreateOrderView>() {
    private val orderInteractor: OrderInteractor by inject()
    private val promoInteractor: PromoInteractor by inject()
    private val dateDelegate: DateTimeDelegate  = get()

    private val currencyMapper = get<CurrencyMapper>()
    private val routeMapper = get<RouteMapper>()
    private val transportTypeMapper: TransportTypeMapper by inject()
    private val transportTypePriceMapper: TransportTypePriceMapper by inject()
    private val userMapper = get<UserMapper>()

    private var user = userMapper.toView(systemInteractor.account.user)
    private val currencies = systemInteractor.currencies.map { currencyMapper.toView(it) }
    private var duration: Int? = null
    private var passengers = MIN_PASSENGERS
    private var children = MIN_CHILDREN
    private var transportTypes: List<TransportTypeModel>? = null
    private var routeModel: RouteModel? = null
    private var polyline: PolylineModel? = null
    private var track: CameraUpdate? = null
    private var promoCode = ""
    private var selectedCurrency = INVALID_CURRENCY_INDEX

    internal var cost: Double? = null

    private var isAfterMinHours = true
    private var isTimeSetByUser = false
    internal lateinit var currentDate: Calendar
    internal var futureHour = 0
    internal var startDate: TripDate = TripDate(Date())
    internal var returnDate: TripDate? = null

    private var flightNumber: String? = null
    private var flightNumberReturn: String? = null
    private var comment: String? = null

    override fun onFirstViewAttach() {
        futureHour = systemInteractor.mobileConfigs.orderMinimumMinutes / 60
        currentDate = getCurrentDatePlusMinimumHours()
        startDate.date = currentDate.time
    }


    @CallSuper
    override fun attachView(view: CreateOrderView) {
        super.attachView(view)
        viewState.setCurrencies(currencies)
        val i = systemInteractor.currentCurrencyIndex
        if (i != -1) setCurrency(i)

        viewState.setUser(user, systemInteractor.account.user.loggedIn)
        viewState.setHintForDateTimeTransfer(orderInteractor.hourlyDuration == null)
        transportTypes?.let { viewState.setTransportTypes(it) }
        checkOrderDateTime()
    }

    private fun checkOrderDateTime() =
            with(orderInteractor) {
                orderStartTime?.let {
                    setPredefinedDate(it, true)
                    startDate.date = it
                    isTimeSetByUser = true
                }
                orderReturnTime?.let {
                    setPredefinedDate(it, false)
                    returnDate = TripDate(it)
                }
            }

    private fun setPredefinedDate(date: Date, field: Boolean) =
        viewState.setDateTimeTransfer(date.simpleFormat(),
                !date.after(getCurrentDatePlusMinimumHours().time),
                field)


    private fun getCurrentDatePlusMinimumHours(): Calendar {
        val calendar = Calendar.getInstance(systemInteractor.locale)
        /* Server must send current locale time */
        calendar.add(Calendar.HOUR_OF_DAY, futureHour)
        calendar.add(Calendar.MINUTE, CreateOrderView.FUTURE_MINUTE)
        return calendar
    }

    fun initMapAndPrices() {
        with(orderInteractor) {
            if (from == null || (to == null && hourlyDuration == null)) {
                Timber.w("routerInteractor init error. from: $from, to: $to, duration: $hourlyDuration")
                return
            }
            else if (hourlyDuration != null) { // not need route info when hourly
                setUIWithoutRoute()
                return
            }
        }

        val from = orderInteractor.from!!.cityPoint
        val to = orderInteractor.to!!.cityPoint
        if (from.point == null || to.point == null) {
            Timber.w("NPE! from: $from, to: $to")
            viewState.setError(ApiException(ApiException.APP_ERROR, "`From` ($from) or `To` {$to} is not set"))
            return
        }
        utils.launchSuspend {
            viewState.blockInterface(true)
            var route: RouteInfo? = null
            fetchData { orderInteractor.getRouteInfo(from.point!!, to.point!!, true, false, systemInteractor.currency.code) }
                    ?.let {
                        route = it
                        duration = it.duration
                    }
            setTransportTypePrices(route?.prices ?: emptyMap())

            routeModel = routeMapper.getView(
                    route?.distance,
                    route?.polyLines,
                    from.name!!,
                    to.name!!,
                    from.point!!,
                    to.point!!,
                    SystemUtils.formatDateTime(startDate.date)
            )

            routeModel?.let {
                polyline = Utils.getPolyline(it)
                track = polyline!!.track
                viewState.setRoute(polyline!!, it, false)
            }
            viewState.blockInterface(false)
        }
    }

    private fun initPrices(returnWay: Boolean){
        val from = orderInteractor.from!!.cityPoint
        val to = orderInteractor.to!!.cityPoint
        if (from.point == null || to.point == null) {
            Timber.w("NPE! from: $from, to: $to")
            viewState.setError(ApiException(ApiException.APP_ERROR, "`From` ($from) or `To` {$to} is not set"))
            return
        }
        utils.launchSuspend {
            fetchData { orderInteractor.getRouteInfo(from.point!!, to.point!!, true, returnWay, systemInteractor.currency.code) }
                    ?.let { setTransportTypePrices(it.prices) }
        }
    }

    private fun setTransportTypePrices(prices: Map<TransportType.ID, TransportTypePrice>){
        transportTypeMapper.prices = prices.mapValues { transportTypePriceMapper.toView(it.value) }
        val newTransportTypes = systemInteractor.transportTypes.map { transportTypeMapper.toView(it) }
        if(transportTypes != null) newTransportTypes.forEach { type -> type.checked = transportTypes?.find { old -> old.id == type.id }?.checked ?: false }
        transportTypes = newTransportTypes
        viewState.setTransportTypes(transportTypes!!)
    }

    private fun setUIWithoutRoute() {
        transportTypeMapper.prices = null
        if (transportTypes == null)
            transportTypes = systemInteractor.transportTypes.map { transportTypeMapper.toView(it) }
        viewState.setTransportTypes(transportTypes!!)
        orderInteractor.from?.let { from ->
            from.cityPoint.point?.let { p ->
                val point = LatLng(p.latitude, p.longitude)
                track = Utils.getCameraUpdateForPin(point)
                viewState.setPinHourlyTransfer(from.address ?: "", from.primary ?: "", point, track!!)
            }
        }
    }

    fun changeDate(newDate: Date, isStartDate: Boolean) {
        if (!isStartDate && returnDate == null) {
            returnDate = TripDate(startDate.date).apply { date.time + DATE_OFFSET }
            initPrices(true)
        }
        val fieldDate = if (isStartDate) startDate.also { isTimeSetByUser = true } else { returnDate!!}   //!!      //enable get transfer only if startDate was set manually
        currentDate = getCurrentDatePlusMinimumHours()
        if (newDate.after(currentDate.time)) {
            isAfterMinHours = false 
            fieldDate.date = newDate
        } else {
            isAfterMinHours = true
            fieldDate.date = currentDate.time
        }
        viewState.setDateTimeTransfer(SystemUtils.formatDateTime(fieldDate.date), isAfterMinHours, isStartDate) //!!...
        if (isStartDate) routeModel?.let {
            it.dateTime = SystemUtils.formatDateTime(fieldDate.date)
            viewState.setRoute(polyline!!, it, true)
        }
    }

    fun clearReturnDate() {
        returnDate = null
        flightNumberReturn = null
        initPrices(false)
    }

    fun changeCurrency(selected: Int) {
        if (selected < currencies.size) {
            val currencyModel = currencies[selected]
            systemInteractor.currency = currencyModel.delegate
            setCurrency(selected)
            saveAccount()
            if (orderInteractor.hourlyDuration == null)
                initPrices(returnDate != null)
        }
    }

    private fun setCurrency(selected: Int){
        if (selected < currencies.size) {
            selectedCurrency = selected
            viewState.setCurrency(currencies[selected].symbol)
        }
    }

    fun changePassengers(count: Int) {
        passengers += count
        if (passengers < MIN_PASSENGERS) passengers = MIN_PASSENGERS
        viewState.setPassengers(passengers)
        logTransferSettingsEvent(Analytics.PASSENGERS_ADDED)
    }

    fun setName(name: String) {
        user.profile.name = name
        checkFields()
    }

    fun setEmail(email: String) {
        user.profile.email = email
        checkFields()
    }

    fun setPhone(phone: String) {
        user.profile.phone = phone
        checkFields()
    }

    fun changeChildren(count: Int) {
        children += count
        if (children < MIN_CHILDREN) children = MIN_CHILDREN
        viewState.setChildren(children)
        logTransferSettingsEvent(Analytics.CHILDREN_ADDED)
    }

    fun setFlightNumber(flightNumber: String, returnWay: Boolean) {
        (if (flightNumber.isEmpty()) null else flightNumber).let {
            if (returnWay) flightNumberReturn = it
            else this.flightNumber = it }
        logTransferSettingsEvent(Analytics.FLIGHT_NUMBER_ADDED)
    }

    fun setPromo(codeText: String) {
        promoCode = codeText
        if (codeText.isEmpty()) viewState.resetPromoView()
    }

    fun checkPromoCode() {
        if (promoCode.isEmpty()) return
        utils.launchSuspend {
            viewState.blockInterface(true)
            val result = utils.asyncAwait { promoInteractor.getDiscountByPromo(promoCode) }
            if (result.error == null) viewState.setPromoResult(result.model.discount) else viewState.setPromoResult(null)
            viewState.blockInterface(false)
        }
    }

    fun setComment(comment: String) {
        if (comment.isEmpty()) this.comment = null else this.comment = comment
        viewState.setComment(comment)
    }

    fun setAgreeLicence(agreeLicence: Boolean) {
        user.termsAccepted = agreeLicence
        systemInteractor.account.user.termsAccepted = true
        checkFields()
    }

    fun showLicenceAgreement() = router.navigateTo(Screens.LicenceAgree)

    fun onGetTransferClick() {
        currentDate = getCurrentDatePlusMinimumHours()
        if (currentDate.time.after(startDate.date)) startDate.date = currentDate.time
        if (!checkFieldsForRequest()) return

        val from = orderInteractor.from!!
        val to = orderInteractor.to
        val transferNew = TransferNew(
            from.cityPoint,
            if (orderInteractor.hourlyDuration != null) DestDuration(orderInteractor.hourlyDuration!!) else DestPoint(to!!.cityPoint),
            Trip(startDate.date, flightNumber),
            returnDate?.let { Trip(if (it.date.after(startDate.date)) it.date else Date(startDate.date.time + DATE_OFFSET) , flightNumberReturn) },
            transportTypes!!.filter { it.checked }.map { it.id },
            passengers,
            children,
            cost?.times(100)?.toInt(),
            comment,
            userMapper.fromView(user),
            promoCode,
            false
        )
        Timber.d("new transfer: $transferNew")

        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val result    = fetchResultOnly { transferInteractor.createTransfer(transferNew) }
            val logResult = fetchResultOnly { systemInteractor.putAccount() }

            if (result.error == null && logResult.error == null) {
                logGetOffers()
                router.replaceScreen(Screens.Offers(result.model.id))
            } else if (result.error != null) {
                logCreateTransfer(Analytics.SERVER_ERROR)
                when {
                    result.error!!.details == "{phone=[taken]}" -> viewState.setError(false, R.string.LNG_PHONE_TAKEN_ERROR)
                    result.error!!.code == ApiException.NETWORK_ERROR -> viewState.setError(false, R.string.LNG_NETWORK_ERROR)
                    else -> viewState.setError(result.error!!)
                }
            } else if (logResult.error != null) viewState.showNotLoggedAlert(result.model.id)

            //404 - есть акк, но не выполнен вход
            //500 - нет акка

            viewState.blockInterface(false)
        }
    }

    private fun checkFieldsForRequest(): Boolean {
        val errorField = when {
            !isTimeSetByUser                                  -> FieldError.TIME_NOT_SELECTED
            !dateDelegate.validate()                          -> FieldError.RETURN_TIME
            !Utils.checkEmail(user.profile.email)             -> FieldError.EMAIL_FIELD
            !Utils.checkPhone(user.profile.phone!!)           -> FieldError.PHONE_FIELD
            transportTypes!!.none { it.checked }              -> FieldError.TRANSPORT_FIELD
            passengers == 0                                   -> FieldError.PASSENGERS_COUNT
            !user.termsAccepted                               -> FieldError.TERMS_ACCEPTED_FIELD
            else                                              -> FieldError.UNKNOWN
        }
        if (errorField == FieldError.UNKNOWN) return true
        logCreateTransfer(errorField.value)
        viewState.showEmptyFieldError(errorField.stringId)
        return false
    }

    /* @TODO: Добавить реакцию на некорректное значение в поле. Отображать, где и что введено некорректно. */
    private fun checkFields() {
        if (transportTypes == null) return
        val typesHasSelected = transportTypes!!.any { it.checked }
        val actionEnabled = typesHasSelected &&
                            Patterns.EMAIL_ADDRESS.matcher(user.profile.email!!).matches() &&
                            Utils.checkPhone(user.profile.phone!!) &&
                            user.termsAccepted
        viewState.setGetTransferEnabled(actionEnabled)
    }

    fun onTransportChosen() {
        checkFields()
        try {
            val tripTime = String.format("%d:%d", duration!! / 60, duration!! % 60)
            val checkedTransport = transportTypes?.filter { it.checked }
            if (!checkedTransport.isNullOrEmpty())
                viewState.setFairPrice(checkedTransport.minBy { it.price!!.minFloat }?.price!!.min, tripTime)
            else viewState.setFairPrice(null, null)
        } catch (e: KotlinNullPointerException) { viewState.setFairPrice(null, null) }
    }

    fun onCenterRouteClick() {
        track?.let { viewState.centerRoute(it) }
        logButtons(Analytics.SHOW_ROUTE_CLICKED)
    }

    fun onBackClick() = onBackCommandClick()

    override fun onBackCommandClick() {
 //       router.navigateTo(Screens.ChangeMode(Screens.PASSENGER_MODE))
        router.exit()
        logButtons(Analytics.BACK_TO_MAP)
    }

    fun redirectToLogin(id: Long) = router.replaceScreen(Screens.LoginToGetOffers(id, user.profile.email))

    fun logButtons(event: String) {
        analytics.logEventToFirebase(event, null)
        analytics.logEventToYandex(event, null)
    }

    fun logTransferSettingsEvent(value: String) {
        val map = mutableMapOf<String, Any>()
        map[Analytics.PARAM_KEY_FIELD] = value

        analytics.logEvent(Analytics.EVENT_TRANSFER_SETTINGS, createStringBundle(Analytics.PARAM_KEY_FIELD, value), map)
    }

    private fun logCreateTransfer(value: String) {
        val bundle = Bundle()
        val map = mutableMapOf<String, Any?>()

        map[Analytics.PARAM_KEY_RESULT] = value
        bundle.putString(Analytics.PARAM_KEY_RESULT, value)

        if (cost != null) {
            bundle.putString(Analytics.VALUE, cost.toString())
            map[Analytics.VALUE] = cost.toString()
        }

        if (selectedCurrency != INVALID_CURRENCY_INDEX) {
            bundle.putString(Analytics.CURRENCY, currencies[selectedCurrency].name)
            map[Analytics.CURRENCY] = currencies[selectedCurrency].name
        }
        duration?.let { bundle.putInt(Analytics.HOURS, it) }
        duration?.let { map[Analytics.HOURS] =  it }

        analytics.logEvent(Analytics.EVENT_TRANSFER, bundle, map)
    }

    private fun logEventAddToCart(value: String) {
        val bundle = Bundle()
        val fbBundle = Bundle()
        val map = mutableMapOf<String, Any?>()
        val afMap = mutableMapOf<String, Any?>()

        bundle.putInt(Analytics.NUMBER_OF_PASSENGERS, passengers)
        map[Analytics.NUMBER_OF_PASSENGERS] = passengers

        bundle.putString(Analytics.ORIGIN, orderInteractor.from?.primary)
        map[Analytics.ORIGIN] = orderInteractor.from?.primary
        bundle.putString(Analytics.DESTINATION, orderInteractor.to?.primary)
        map[Analytics.DESTINATION] = orderInteractor.to?.primary

        bundle.putString(Analytics.TRAVEL_CLASS, transportTypes?.filter { it.checked }?.joinToString())
        map[Analytics.TRAVEL_CLASS] = transportTypes?.filter { it.checked }?.joinToString()

        duration?.let { bundle.putInt(Analytics.HOURS, it) }
        duration?.let { map[Analytics.HOURS] =  it }

        when {
            duration != null -> Analytics.TRIP_HOURLY
            returnDate != null -> Analytics.TRIP_ROUND
            else -> Analytics.TRIP_DESTINATION
        }.let {
            bundle.putString(Analytics.TRIP_TYPE, it)
            map[Analytics.TRIP_TYPE] = it
        }

        fbBundle.putAll(bundle)
        afMap.putAll(map)

        if (cost != null) {
            bundle.putString(Analytics.VALUE, cost.toString())
            map[Analytics.VALUE] = cost.toString()
        }

        if (selectedCurrency != INVALID_CURRENCY_INDEX) {
            val currency = currencies[selectedCurrency].name
            bundle.putString(Analytics.CURRENCY, currency)
            fbBundle.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, currency)
            map[Analytics.CURRENCY] = currency
            afMap[AFInAppEventParameterName.CURRENCY] = currency
        }

        analytics.logEventToFirebase(value, bundle)
        analytics.logEventToFacebook(AppEventsConstants.EVENT_NAME_ADDED_TO_CART, fbBundle)
        analytics.logEventToYandex(value, map)
        analytics.logEventToAppsFlyer(AFInAppEventType.ADD_TO_CART, afMap)
    }

    private fun logStartScreenOrder() {
        val pair = Pair(Analytics.ORDER_CREATED_FROM, if (systemInteractor.startScreenOrder) Analytics.FROM_FORM else Analytics.FROM_MAP)
        analytics.logEvent(Analytics.EVENT_TRANSFER, Bundle().apply {
            putString(pair.first, pair.second)
        }, mapOf(pair))
    }


    private fun logGetOffers() {
        logCreateTransfer(Analytics.RESULT_SUCCESS)
        logEventAddToCart(Analytics.EVENT_ADD_TO_CART)
        logStartScreenOrder()
    }


    companion object {
        private const val MIN_PASSENGERS = 0
        private const val MIN_CHILDREN   = 0

        private const val INVALID_CURRENCY_INDEX = -1
        private const val DATE_OFFSET = 1000 * 60 * 5 // 5 minutes
    }
}
