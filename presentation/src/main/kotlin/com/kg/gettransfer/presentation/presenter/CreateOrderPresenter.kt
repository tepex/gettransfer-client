package com.kg.gettransfer.presentation.presenter

import android.os.Bundle
import android.support.annotation.CallSuper

import android.util.Patterns

import com.arellomobile.mvp.InjectViewState

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.interactor.OfferInteractor
import com.kg.gettransfer.domain.interactor.PromoInteractor
import com.kg.gettransfer.domain.interactor.RouteInteractor
import com.kg.gettransfer.domain.interactor.TransferInteractor

import com.kg.gettransfer.domain.model.CityPoint
import com.kg.gettransfer.domain.model.Dest
import com.kg.gettransfer.domain.model.DestDuration
import com.kg.gettransfer.domain.model.DestPoint
import com.kg.gettransfer.domain.model.Trip

import com.kg.gettransfer.presentation.model.Mappers
import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.model.TransportPrice
import com.kg.gettransfer.presentation.model.TransportTypeModel
import com.kg.gettransfer.presentation.model.UserModel

import com.kg.gettransfer.presentation.ui.Utils

import com.kg.gettransfer.presentation.view.CreateOrderView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics

import java.text.Format
import java.text.SimpleDateFormat

import java.util.Calendar
import java.util.Date

import org.koin.standalone.inject

import timber.log.Timber

@InjectViewState
class CreateOrderPresenter: BasePresenter<CreateOrderView>() {
    private val routeInteractor: RouteInteractor by inject()
    private val transferInteractor: TransferInteractor by inject()
    private val promoInteractor: PromoInteractor by inject()
    private val offersInteractor: OfferInteractor by inject()

    private var user: UserModel = Mappers.getUserModel(systemInteractor.account)
    private val currencies = Mappers.getCurrenciesModels(systemInteractor.currencies)
    private var duration: Int? = null
    private var passengers: Int = MIN_PASSENGERS
    private var children: Int = MIN_CHILDREN
    private var dateTimeFormat: Format? = null
    private var transportTypes: List<TransportTypeModel>? = null
    private var routeModel: RouteModel? = null
    private var polyline: PolylineModel? = null
    private var track: CameraUpdate? = null
    private var promoCode: String = ""
    private var selectedCurrency: Int = -2

    internal var cost: Double? = null

    private var isAfter4Hours = true
    internal lateinit var currentDate: Calendar
    internal var date: Date = Date()
        set(value) {
            field = value
            dateTimeFormat?.let { viewState.setDateTimeTransfer(it.format(date), isAfter4Hours) }
        }

    private var flightNumber: String? = null
    private var comment: String? = null

    companion object {
        @JvmField val MIN_PASSENGERS    = 1
        @JvmField val MIN_CHILDREN      = 0
        /* Пока сервевер не присылает минимальный временной промежуток до заказа */
        @JvmField val FUTURE_HOUR       = 4
        @JvmField val FUTURE_MINUTE     = 5

        const val EMAIL_FIELD           = "email"
        const val PHONE_FIELD           = "phone"
        const val TRANSPORT_FIELD       = "transport"
        const val TERMS_ACCEPTED_FIELD  = "terms_accepted"

        //CreateTransfer Params:
        @JvmField val NO_TRANSPORT_SELECTED = "no_transport_type"
        @JvmField val NO_EMAIL = "invalid_email"
        @JvmField val NO_PHONE = "invalid_phone"
        @JvmField val NO_NAME  = "invalid_name"
        @JvmField val NO_LICENSE_ACCEPTED = "license_not_accepted"
    }

    override fun onFirstViewAttach() {
        currentDate = getCurrentDatePlus4Hours()
        date = currentDate.time
    }

    private fun getCurrentDatePlus4Hours(): Calendar {
        val calendar = Calendar.getInstance(systemInteractor.locale)
        /* Server must send current locale time */
        calendar.add(Calendar.HOUR_OF_DAY, FUTURE_HOUR)
        calendar.add(Calendar.MINUTE, FUTURE_MINUTE)
        return calendar
    }

    fun initMapAndPrices() {

        routeInteractor.apply {
            if (from == null || (to == null && hourlyDuration == null)){
                Timber.d("routerInteractor init error. from: $from, to: $to, duration: $hourlyDuration")
                return
            }
            else if (hourlyDuration != null) { // not need route info when hourly
                setUIWithoutRoute()
                return
            }
        }

        utils.launchSuspend {
            viewState.blockInterface(true)
            val from = routeInteractor.from!!.cityPoint
            val to = routeInteractor.to!!.cityPoint

            val result = utils.asyncAwait { routeInteractor.getRouteInfo(from.point!!, to.point!!, true, false) }
            if(result.error != null) viewState.setError(result.error!!)
            else {
                duration = result.model.duration

                val prices: Map<String, TransportPrice> = result.model.prices.map { p -> p.tranferId to TransportPrice(p.min, p.max, p.minFloat) }.toMap()
                if(transportTypes == null)
                    transportTypes = systemInteractor.transportTypes.map { Mappers.getTransportTypeModel(it, prices) }
                viewState.setTransportTypes(transportTypes!!)
                routeModel = Mappers.getRouteModel(result.model.distance,
                                                   systemInteractor.distanceUnit,
                                                   result.model.polyLines,
                                                   from.name!!,
                                                   to.name!!,
                                                   from.point!!,
                                                   to.point!!,
                                                   SimpleDateFormat(Utils.DATE_TIME_PATTERN).format(date))
            }
            routeModel?.let {
                polyline = Utils.getPolyline(it)
                track = polyline!!.track
                viewState.setRoute(polyline!!, it, false)
            }
            viewState.blockInterface(false)
        }
    }

    private fun setUIWithoutRoute() {
        transportTypes = systemInteractor.transportTypes.map { Mappers.getTransportTypeModel(it, null) }
        viewState.setTransportTypes(transportTypes!!)
        routeInteractor.from!!.let {
            viewState.setPinHourlyTransfer(it.address?:"", it.primary?:"", it.cityPoint.point.let { p -> LatLng(p!!.latitude, p.longitude) } ) }
    }

    fun changeDate(newDate: Date) {
        currentDate = getCurrentDatePlus4Hours()
        if(newDate.after(currentDate.time)) {
            isAfter4Hours = false
            date = newDate
        } else {
            isAfter4Hours = true
            date = currentDate.time
        }
        routeModel?.let {
            it.dateTime = SimpleDateFormat(Utils.DATE_TIME_PATTERN).format(date)
            viewState.setRoute(polyline!!, it, true)
        }
    }

    @CallSuper
    override fun attachView(view: CreateOrderView) {
        super.attachView(view)
        dateTimeFormat = SimpleDateFormat(Utils.DATE_TIME_PATTERN, systemInteractor.locale)
        viewState.setCurrencies(currencies)
        val i = systemInteractor.currentCurrencyIndex
        if(i != -1) changeCurrency(i)

        viewState.setUser(user, systemInteractor.account.user.loggedIn)
        viewState.setDateTimeTransfer(Utils.getFormattedDate(systemInteractor.locale, date), isAfter4Hours)
        transportTypes?.let { viewState.setTransportTypes(it) }
    }

    fun changeCurrency(selected: Int) {
        selectedCurrency = selected
        viewState.setCurrency(currencies[selected].symbol)
    }

    fun changePassengers(count: Int) {
        passengers += count
        if(passengers < MIN_PASSENGERS) passengers = MIN_PASSENGERS
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
        if(children < MIN_CHILDREN) children = MIN_CHILDREN
        viewState.setChildren(children)
        logTransferSettingsEvent(Analytics.CHILDREN_ADDED)
    }

    fun setFlightNumber(flightNumber: String) {
        if(flightNumber.isEmpty()) this.flightNumber = null else this.flightNumber = flightNumber
        logTransferSettingsEvent(Analytics.FLIGHT_NUMBER_ADDED)
    }

    fun setPromo(codeText: String) {
        promoCode = codeText
        if(codeText.isEmpty()) viewState.resetPromoView()
    }

    fun checkPromoCode() {
        if(promoCode.isEmpty()) return
        utils.launchSuspend {
            viewState.blockInterface(true)
            val result = utils.asyncAwait { promoInteractor.getDiscountByPromo(promoCode) }
            if(result.error == null) viewState.setPromoResult(result.model.discount)
            else viewState.setPromoResult(null)
            viewState.blockInterface(false)
        }
    }

    fun setComment(comment: String) {
        if(comment.isEmpty()) this.comment = null else this.comment = comment
        viewState.setComment(comment)
    }

    fun setAgreeLicence(agreeLicence: Boolean) {
        user.termsAccepted = agreeLicence
        checkFields()
    }

    fun showLicenceAgreement() = router.navigateTo(Screens.LicenceAgree)

    fun onGetTransferClick() {
        currentDate = getCurrentDatePlus4Hours()
        if(currentDate.time.after(date)) date = currentDate.time

        if(!checkFieldsForRequest()) return

        val trip = Trip(date, flightNumber)
        /* filter */
        val selectedTransportTypes = transportTypes!!.filter { it.checked }.map { it.id }

        Timber.d("from: %s", routeInteractor.from)
        Timber.d("to: %s", routeInteractor.to)
        Timber.d("trip: %s", trip)
        Timber.d("transport types: %s", selectedTransportTypes)
        Timber.d("user: $user")
        Timber.d("passenger price: $cost")
        Timber.d("date: $date")
        Timber.d("passengers: $passengers")
        Timber.d("children: $children")
        Timber.d("flightNumber: $flightNumber")
        Timber.d("comment: $comment")

//        if(routeInteractor.from == null || routeInteractor.to == null) return
        val from = routeInteractor.from!!
        val to = routeInteractor.to
        val dest: Dest<CityPoint, Int> = if(routeInteractor.hourlyDuration != null) DestDuration(routeInteractor.hourlyDuration!!) else DestPoint(to!!.cityPoint)
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val result = utils.asyncAwait {
                transferInteractor.createTransfer(Mappers.getTransferNew(from.cityPoint,
                                                                         dest,
                                                                         trip,
                                                                         null,
                                                                         selectedTransportTypes,
                                                                         passengers,
                                                                         children,
                                                                         cost,
                                                                         comment,
                                                                         Mappers.getUser(user),
                                                                         promoCode,
                                                                         false))
            }

            val logResult = utils.asyncAwait { systemInteractor.putAccount() }
            if(result.error == null && logResult.error == null) {
                logCreateTransfer(Analytics.RESULT_SUCCESS)
                router.replaceScreen(Screens.Offers(result.model.id))
            } else if(result.error != null) {
                logCreateTransfer(Analytics.SERVER_ERROR)
                when {
                    result.error!!.details == "{phone=[taken]}" -> viewState.setError(false, R.string.LNG_PHONE_TAKEN_ERROR)
                    else -> viewState.setError(result.error!!)
                }
            } else if(logResult.error != null) viewState.showNotLoggedAlert(result.model.id)
            viewState.blockInterface(false)
        }
    }

    private fun checkFieldsForRequest(): Boolean {
        val errorFiled: String
        if (!Utils.checkEmail(user.profile.email)) {
            errorFiled = EMAIL_FIELD
        } else if (!Utils.checkPhone(user.profile.phone!!)) {
            errorFiled = PHONE_FIELD
        } else if (transportTypes != null &&
                !transportTypes!!.any { it.checked }) {
            errorFiled = TRANSPORT_FIELD
        } else if (!user.termsAccepted) {
            errorFiled = TERMS_ACCEPTED_FIELD
        } else return true
        logCreateTransfer(Mappers.getAnalyticsParam(errorFiled))
        viewState.showEmptyFieldError(errorFiled)
        return false
    }

    /* @TODO: Добавить реакцию на некорректное значение в поле. Отображать, где и что введено некорректно. */
    private fun checkFields() {
        if(transportTypes == null) return
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
            if(!checkedTransport.isNullOrEmpty())
                viewState.setFairPrice(checkedTransport.minBy { it.price!!.unitPrice }?.price!!.min, tripTime)
            else viewState.setFairPrice(null, null)
        } catch(e: KotlinNullPointerException) { viewState.setFairPrice(null, null) }
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

    fun redirectToLogin(id: Long) = router.navigateTo(Screens.LoginToGetOffers(id, user.profile.email))

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

        if (cost != null) {
            bundle.putString(Analytics.VALUE, cost.toString())
            map[Analytics.VALUE] = cost.toString()

            bundle.putString(Analytics.CURRENCY, currencies[selectedCurrency].name)
            map[Analytics.CURRENCY] = currencies[selectedCurrency].name
        }
        analytics.logEvent(Analytics.EVENT_TRANSFER, bundle, map)

        logEventAddToCart(bundle, map)
    }

    private fun logEventAddToCart(bundle: Bundle, map: MutableMap<String, Any?>) {
        bundle.putInt(Analytics.NUMBER_OF_PASSENGERS, passengers)
        map[Analytics.NUMBER_OF_PASSENGERS] = passengers

        bundle.putString(Analytics.ORIGIN, routeInteractor.from?.primary)
        map[Analytics.ORIGIN] = routeInteractor.from?.primary
        bundle.putString(Analytics.DESTINATION, routeInteractor.to?.primary)
        map[Analytics.DESTINATION] = routeInteractor.to?.primary

        bundle.putString(Analytics.TRAVEL_CLASS, transportTypes?.filter { it.checked }?.joinToString())
        map[Analytics.TRAVEL_CLASS] = transportTypes?.filter { it.checked }?.joinToString()

        analytics.logEvent(Analytics.EVENT_ADD_TO_CART, bundle, map)
    }
}
