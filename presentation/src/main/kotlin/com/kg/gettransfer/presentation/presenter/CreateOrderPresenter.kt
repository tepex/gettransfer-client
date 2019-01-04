package com.kg.gettransfer.presentation.presenter

import android.os.Bundle
import android.support.annotation.CallSuper

import android.util.Patterns

import com.arellomobile.mvp.InjectViewState
import com.facebook.appevents.AppEventsConstants

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.ApiException

import com.kg.gettransfer.domain.interactor.PromoInteractor
import com.kg.gettransfer.domain.interactor.RouteInteractor
import com.kg.gettransfer.domain.interactor.TransferInteractor

import com.kg.gettransfer.domain.model.DestDuration
import com.kg.gettransfer.domain.model.DestPoint
import com.kg.gettransfer.domain.model.TransferNew
import com.kg.gettransfer.domain.model.Trip
import com.kg.gettransfer.domain.model.TransportType
import com.kg.gettransfer.domain.model.TransportTypePrice

import com.kg.gettransfer.presentation.mapper.CurrencyMapper
import com.kg.gettransfer.presentation.mapper.RouteMapper
import com.kg.gettransfer.presentation.mapper.TransportTypeMapper
import com.kg.gettransfer.presentation.mapper.TransportTypePriceMapper
import com.kg.gettransfer.presentation.mapper.UserMapper

import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.model.TransportTypeModel

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
    private val routeInteractor: RouteInteractor by inject()
    private val transferInteractor: TransferInteractor by inject()
    private val promoInteractor: PromoInteractor by inject()

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
    internal var date: Date = Date()

    private var flightNumber: String? = null
    private var comment: String? = null

    override fun onFirstViewAttach() {
        futureHour = systemInteractor.mobileConfigs.orderMinimumMinutes / 60
        currentDate = getCurrentDatePlusMinimumHours()
        date = currentDate.time
    }

    private fun getCurrentDatePlusMinimumHours(): Calendar {
        val calendar = Calendar.getInstance(systemInteractor.locale)
        /* Server must send current locale time */
        calendar.add(Calendar.HOUR_OF_DAY, futureHour)
        calendar.add(Calendar.MINUTE, CreateOrderView.FUTURE_MINUTE)
        return calendar
    }

    fun initMapAndPrices() {
        routeInteractor.apply {
            if (from == null || (to == null && hourlyDuration == null)) {
                Timber.w("routerInteractor init error. from: $from, to: $to, duration: $hourlyDuration")
                return
            }
            else if (hourlyDuration != null) { // not need route info when hourly
                setUIWithoutRoute()
                return
            }
        }

        val from = routeInteractor.from!!.cityPoint
        val to = routeInteractor.to!!.cityPoint
        if (from.point == null || to.point == null) {
            Timber.w("NPE! from: $from, to: $to")
            viewState.setError(ApiException(ApiException.APP_ERROR, "`From` ($from) or `To` {$to} is not set"))
            return
        }
        utils.launchSuspend {
            viewState.blockInterface(true)
            val result = utils.asyncAwait { routeInteractor.getRouteInfo(from.point!!, to.point!!, true, false, systemInteractor.currency.currencyCode) }
            if (result.error != null) viewState.setError(result.error!!)
            else {
                val route = result.model
                duration = route.duration

                setTransportTypePrices(result.model.prices)

                routeModel = routeMapper.getView(
                    route.distance,
                    route.polyLines,
                    from.name!!,
                    to.name!!,
                    from.point!!,
                    to.point!!,
                    SystemUtils.formatDateTime(date)
                )
            }
            routeModel?.let {
                polyline = Utils.getPolyline(it)
                track = polyline!!.track
                viewState.setRoute(polyline!!, it, false)
            }
            viewState.blockInterface(false)
        }
    }

    private fun initPrices(){
        val from = routeInteractor.from!!.cityPoint
        val to = routeInteractor.to!!.cityPoint
        if (from.point == null || to.point == null) {
            Timber.w("NPE! from: $from, to: $to")
            viewState.setError(ApiException(ApiException.APP_ERROR, "`From` ($from) or `To` {$to} is not set"))
            return
        }
        utils.launchSuspend {
            val result = utils.asyncAwait { routeInteractor.getRouteInfo(from.point!!, to.point!!, true, false, systemInteractor.currency.currencyCode) }
            if (result.error != null) viewState.setError(result.error!!)
            else setTransportTypePrices(result.model.prices)
        }
    }

    private fun setTransportTypePrices(prices: Map<TransportType.ID, TransportTypePrice>){
        transportTypeMapper.prices = prices.mapValues { transportTypePriceMapper.toView(it.value) }
        transportTypes = systemInteractor.transportTypes.map { transportTypeMapper.toView(it) }
        viewState.setTransportTypes(transportTypes!!)
    }

    private fun setUIWithoutRoute() {
        if (transportTypes == null)
            transportTypes = systemInteractor.transportTypes.map { transportTypeMapper.toView(it) }
        viewState.setTransportTypes(transportTypes!!)
        routeInteractor.from?.let { from ->
            from.cityPoint.point?.let { p ->
                val point = LatLng(p.latitude, p.longitude)
                track = Utils.getCameraUpdateForPin(point)
                viewState.setPinHourlyTransfer(from.address ?: "", from.primary ?: "", point, track!!)
            }
        }
    }

    fun changeDate(newDate: Date) {
        isTimeSetByUser = true
        currentDate = getCurrentDatePlusMinimumHours()
        if (newDate.after(currentDate.time)) {
            isAfterMinHours = false
            date = newDate
        } else {
            isAfterMinHours = true
            date = currentDate.time
        }
        viewState.setDateTimeTransfer(SystemUtils.formatDateTime(date), isAfterMinHours)
        routeModel?.let {
            it.dateTime = SystemUtils.formatDateTime(date)
            viewState.setRoute(polyline!!, it, true)
        }
    }

    @CallSuper
    override fun attachView(view: CreateOrderView) {
        super.attachView(view)
        viewState.setCurrencies(currencies)
        val i = systemInteractor.currentCurrencyIndex
        if (i != -1) setCurrency(i)

        viewState.setUser(user, systemInteractor.account.user.loggedIn)
        viewState.setHintForDateTimeTransfer()
        transportTypes?.let { viewState.setTransportTypes(it) }
    }

    fun changeCurrency(selected: Int) {
        if (selected < currencies.size) {
            val currencyModel = currencies[selected]
            systemInteractor.currency = currencyModel.delegate
            setCurrency(selected)
            saveAccount()
            initPrices()
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

    fun setFlightNumber(flightNumber: String) {
        if (flightNumber.isEmpty()) this.flightNumber = null else this.flightNumber = flightNumber
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
        if (currentDate.time.after(date)) date = currentDate.time

        if (!checkFieldsForRequest()) return

        val from = routeInteractor.from!!
        val to = routeInteractor.to
        val transferNew = TransferNew(
            from.cityPoint,
            if (routeInteractor.hourlyDuration != null) DestDuration(routeInteractor.hourlyDuration!!) else DestPoint(to!!.cityPoint),
            Trip(date, flightNumber),
            null,
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
            val result = utils.asyncAwait { transferInteractor.createTransfer(transferNew) }

            val logResult = utils.asyncAwait { systemInteractor.putAccount() }
            if (result.error == null && logResult.error == null) {
                logCreateTransfer(Analytics.RESULT_SUCCESS)
                logEventAddToCart(Analytics.EVENT_ADD_TO_CART)
                router.replaceScreen(Screens.Offers(result.model.id))
            } else if (result.error != null) {
                logCreateTransfer(Analytics.SERVER_ERROR)
                when {
                    result.error!!.details == "{phone=[taken]}" -> viewState.setError(false, R.string.LNG_PHONE_TAKEN_ERROR)
                    else -> viewState.setError(result.error!!)
                }
            } else if (logResult.error != null) viewState.showNotLoggedAlert(result.model.id)
            viewState.blockInterface(false)
        }
    }

    private fun checkFieldsForRequest(): Boolean {
        val errorField = when {
            !isTimeSetByUser                        -> FieldError.TIME_NOT_SELECTED
            !Utils.checkEmail(user.profile.email)   -> FieldError.EMAIL_FIELD
            !Utils.checkPhone(user.profile.phone!!) -> FieldError.PHONE_FIELD
            !transportTypes!!.any { it.checked }    -> FieldError.TRANSPORT_FIELD
            passengers == 0                         -> FieldError.PASSENGERS_COUNT
            !user.termsAccepted                     -> FieldError.TERMS_ACCEPTED_FIELD
            else                                    -> FieldError.UNKNOWN
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
                viewState.setFairPrice(checkedTransport?.minBy { it.price!!.minFloat }?.price!!.min, tripTime)
            else viewState.setFairPrice(null, null)
        } catch (e: KotlinNullPointerException) { viewState.setFairPrice(null, null) }
    }

    fun onCenterRouteClick() {
        track?.let { viewState.centerRoute(it) }
        logEventMain(Analytics.SHOW_ROUTE_CLICKED)
    }

    fun onBackClick() = onBackCommandClick()

    override fun onBackCommandClick() {
 //       router.navigateTo(Screens.ChangeMode(Screens.PASSENGER_MODE))
        router.exit()
        logEventMain(Analytics.BACK_CLICKED)
    }

    fun redirectToLogin(id: Long) = router.replaceScreen(Screens.LoginToGetOffers(id, user.profile.email))

    fun logEventMain(value: String) {
        val map = mutableMapOf<String, Any>()
        map[Analytics.PARAM_KEY_NAME] = value

        analytics.logEvent(Analytics.EVENT_MAIN, createStringBundle(Analytics.PARAM_KEY_NAME, value), map)
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

        if (cost != null && selectedCurrency != INVALID_CURRENCY_INDEX) {
            bundle.putString(Analytics.VALUE, cost.toString())
            map[Analytics.VALUE] = cost.toString()

            bundle.putString(Analytics.CURRENCY, currencies[selectedCurrency].name)
            map[Analytics.CURRENCY] = currencies[selectedCurrency].name
        }
        analytics.logEvent(Analytics.EVENT_TRANSFER, bundle, map)
    }

    private fun logEventAddToCart(value: String) {
        val bundle = Bundle()
        val fbBundle = Bundle()
        val map = mutableMapOf<String, Any?>()

        bundle.putInt(Analytics.NUMBER_OF_PASSENGERS, passengers)
        map[Analytics.NUMBER_OF_PASSENGERS] = passengers

        bundle.putString(Analytics.ORIGIN, routeInteractor.from?.primary)
        map[Analytics.ORIGIN] = routeInteractor.from?.primary
        bundle.putString(Analytics.DESTINATION, routeInteractor.to?.primary)
        map[Analytics.DESTINATION] = routeInteractor.to?.primary

        bundle.putString(Analytics.TRAVEL_CLASS, transportTypes?.filter { it.checked }?.joinToString())
        map[Analytics.TRAVEL_CLASS] = transportTypes?.filter { it.checked }?.joinToString()

        fbBundle.putAll(bundle)

        if (cost != null) {
            bundle.putString(Analytics.VALUE, cost.toString())
            map[Analytics.VALUE] = cost.toString()

            bundle.putString(Analytics.CURRENCY, currencies[selectedCurrency].name)
            fbBundle.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, currencies[selectedCurrency].name)
            map[Analytics.CURRENCY] = currencies[selectedCurrency].name
        }

        analytics.logEventToFirebase(value, bundle)
        analytics.logEventToFacebook(AppEventsConstants.EVENT_NAME_ADDED_TO_CART, fbBundle)
        analytics.logEventToYandex(value, map)
    }

    companion object {
        private const val MIN_PASSENGERS = 0
        private const val MIN_CHILDREN   = 0

        private const val INVALID_CURRENCY_INDEX = -1
    }
}
