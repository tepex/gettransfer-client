package com.kg.gettransfer.presentation.presenter

import android.os.Bundle
import android.support.annotation.CallSuper

import android.util.Patterns

import com.arellomobile.mvp.InjectViewState

import com.google.android.gms.maps.CameraUpdate

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.interactor.OfferInteractor
import com.kg.gettransfer.domain.interactor.PromoInteractor
import com.kg.gettransfer.domain.interactor.RouteInteractor
import com.kg.gettransfer.domain.interactor.TransferInteractor

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
import com.kg.gettransfer.utilities.Analytics.Companion.BACK_CLICKED
import com.kg.gettransfer.utilities.Analytics.Companion.CHILDREN_ADDED
import com.kg.gettransfer.utilities.Analytics.Companion.CURRENCY
import com.kg.gettransfer.utilities.Analytics.Companion.DESTINATION
import com.kg.gettransfer.utilities.Analytics.Companion.EVENT_ADD_TO_CART
import com.kg.gettransfer.utilities.Analytics.Companion.EVENT_MAIN
import com.kg.gettransfer.utilities.Analytics.Companion.EVENT_SETTINGS
import com.kg.gettransfer.utilities.Analytics.Companion.EVENT_TRANSFER
import com.kg.gettransfer.utilities.Analytics.Companion.EVENT_TRANSFER_SETTINGS
import com.kg.gettransfer.utilities.Analytics.Companion.FLIGHT_NUMBER_ADDED
import com.kg.gettransfer.utilities.Analytics.Companion.INVALID_EMAIL
import com.kg.gettransfer.utilities.Analytics.Companion.INVALID_NAME
import com.kg.gettransfer.utilities.Analytics.Companion.INVALID_PHONE
import com.kg.gettransfer.utilities.Analytics.Companion.LICENSE_NOT_ACCEPTED
import com.kg.gettransfer.utilities.Analytics.Companion.NO_TRANSPORT_TYPE
import com.kg.gettransfer.utilities.Analytics.Companion.NUMBER_OF_PASSENGERS
import com.kg.gettransfer.utilities.Analytics.Companion.ORIGIN
import com.kg.gettransfer.utilities.Analytics.Companion.PARAM_KEY_FIELD
import com.kg.gettransfer.utilities.Analytics.Companion.PARAM_KEY_NAME
import com.kg.gettransfer.utilities.Analytics.Companion.PARAM_KEY_RESULT
import com.kg.gettransfer.utilities.Analytics.Companion.PASSENGERS_ADDED
import com.kg.gettransfer.utilities.Analytics.Companion.RESULT_SUCCESS
import com.kg.gettransfer.utilities.Analytics.Companion.SHOW_ROUTE_CLICKED
import com.kg.gettransfer.utilities.Analytics.Companion.TRAVEL_CLASS
import com.kg.gettransfer.utilities.Analytics.Companion.VALUE

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
        const val NAME_FIELD            = "name"
        const val PHONE_FIELD           = "phone"
        const val TRANSPORT_FIELD       = "transport"
        const val TERMS_ACCEPTED_FIELD  = "terms_accepted"

        //CreateTransfer Params:
        @JvmField val NO_TRANSPORT_SELECTED = "no_transport_type"
        @JvmField val NO_EMAIL = "invalid_email"
        @JvmField val NO_PHONE = "invalid_phone"
        @JvmField val NO_NAME  = "invalid_name"
        @JvmField val NO_LICENSE_ACCEPTED = "license_not_accepted"
        @JvmField val SERVER_ERROR = "server_error"
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
        utils.launchSuspend {
            viewState.blockInterface(true)
            val from = routeInteractor.from!!.cityPoint
            val to = routeInteractor.to!!.cityPoint

            val result = utils.asyncAwait { routeInteractor.getRouteInfo(from.point!!, to.point!!, true, false) }
            if(result.error != null) viewState.setError(result.error!!)
            else {
                duration = result.model.duration
                
                var prices: Map<String, TransportPrice> = result.model.prices.map { p -> p.tranferId to TransportPrice(p.min, p.max, p.minFloat) }.toMap()
                if(transportTypes == null)
                    transportTypes = systemInteractor.transportTypes.map { Mappers.getTransportTypeModel(it, prices) }
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
                viewState.setTransportTypes(transportTypes!!)
                polyline = Utils.getPolyline(it)
                track = polyline?.track
                viewState.setRoute(polyline!!, it, false)
            }
            viewState.blockInterface(false)
        }
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
        logTransferSettingsEvent(PASSENGERS_ADDED)
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
        logTransferSettingsEvent(CHILDREN_ADDED)
    }
    
    fun setFlightNumber(flightNumber: String) {
        if(flightNumber.isEmpty()) this.flightNumber = null else this.flightNumber = flightNumber
        logTransferSettingsEvent(FLIGHT_NUMBER_ADDED)
    }

    fun setPromo(codeText: String) {
        promoCode = codeText
        if(codeText.isEmpty()) viewState.resetPromoView()
    }

    fun checkPromoCode() {
        if(promoCode.isNullOrEmpty()) return
        utils.launchSuspend {
            viewState.blockInterface(true)
            val result = utils.asyncAwait { promoInteractor.getDiscountByPromo(promoCode!!) }
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
        Timber.d("to: %s", routeInteractor.to!!)
        Timber.d("trip: %s", trip)
        Timber.d("transport types: %s", selectedTransportTypes)
        Timber.d("user: $user")
        Timber.d("passenger price: $cost")
        Timber.d("date: $date")
        Timber.d("passengers: $passengers")
        Timber.d("children: $children")
        Timber.d("flightNumber: $flightNumber")
        Timber.d("comment: $comment")

        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val result = utils.asyncAwait {
                transferInteractor.createTransfer(Mappers.getTransferNew(routeInteractor.from!!.cityPoint,
                                                                         routeInteractor.to!!.cityPoint,
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
            if(result.error != null) {
                logCreateTransfer(SERVER_ERROR)
                when {
                    result.error!!.isNotLoggedIn() -> router.navigateTo(Screens.Login(Screens.OFFERS, user.profile.email))
                    result.error!!.details == "{phone=[taken]}" -> viewState.setError(false, R.string.LNG_PHONE_TAKEN_ERROR)
                    else -> viewState.setError(result.error!!)
                }
            }
            else {
                logCreateTransfer(RESULT_SUCCESS)
                router.navigateTo(Screens.Offers(result.model.id))
            }
            viewState.blockInterface(false)
        }
    }

    private fun checkFieldsForRequest(): Boolean {
        var errorFiled: String
        if (!Utils.checkEmail(user.profile.email)) {
            errorFiled = EMAIL_FIELD
            logCreateTransfer(INVALID_EMAIL)
        } else if (user.profile.name.isNullOrBlank()) {
            errorFiled = NAME_FIELD
            logCreateTransfer(INVALID_NAME)
        } else if (!Utils.checkPhone(user.profile.phone!!)) {
            errorFiled = PHONE_FIELD
            logCreateTransfer(INVALID_PHONE)
        } else if (transportTypes != null &&
                !transportTypes!!.any { it.checked }) {
            errorFiled = TRANSPORT_FIELD
            logCreateTransfer(NO_TRANSPORT_TYPE)
        } else if (!user.termsAccepted) {
            errorFiled = TERMS_ACCEPTED_FIELD
            logCreateTransfer(LICENSE_NOT_ACCEPTED)
        } else return true
        viewState.showEmptyFieldError(errorFiled)
        return false
    }
    
    /* @TODO: Добавить реакцию на некорректное значение в поле. Отображать, где и что введено некорректно. */
    fun checkFields() {
        if(transportTypes == null) return
        val typesHasSelected = transportTypes!!.filter { it.checked }.size > 0
        val actionEnabled = typesHasSelected &&
                            !user.profile.name.isNullOrBlank() &&
                            !user.profile.email.isNullOrBlank() &&
                            !user.profile.email.isNullOrBlank() &&
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
        viewState.centerRoute(track!!)
        logEventMain(SHOW_ROUTE_CLICKED)
    }

    fun onBackClick() {
        router.navigateTo(Screens.ChangeMode(Screens.PASSENGER_MODE))
        logEventMain(BACK_CLICKED)
    }

    override fun onBackCommandClick() {
        router.navigateTo(Screens.ChangeMode(Screens.PASSENGER_MODE))
        logEventMain(BACK_CLICKED)
    }

    fun logEventMain(value: String) {
        val map = HashMap<String, Any>()
        map[PARAM_KEY_NAME] = value

        analytics.logEvent(EVENT_MAIN, createStringBundle(PARAM_KEY_NAME, value), map)
    }

    fun logTransferSettingsEvent(value: String) {
        val map = HashMap<String, Any>()
        map[PARAM_KEY_FIELD] = value

        analytics.logEvent(EVENT_TRANSFER_SETTINGS, createStringBundle(PARAM_KEY_FIELD, value), map)
    }

    private fun logCreateTransfer(value: String) {
        val bundle = Bundle()
        val map = HashMap<String, Any?>()

        map[PARAM_KEY_RESULT] = value
        bundle.putString(PARAM_KEY_RESULT, value)

        if (cost != null) {
            bundle.putString(VALUE, cost.toString())
            map[VALUE] = cost.toString()

            bundle.putString(CURRENCY, currencies[selectedCurrency].name)
            map[CURRENCY] = currencies[selectedCurrency].name
        }
        analytics.logEvent(EVENT_TRANSFER, bundle, map)

        logEventAddToCart(bundle, map)
    }

    private fun logEventAddToCart(bundle: Bundle, map: HashMap<String, Any?>) {
        bundle.putInt(NUMBER_OF_PASSENGERS, passengers)
        map[NUMBER_OF_PASSENGERS] = passengers

        bundle.putString(ORIGIN, routeInteractor.from?.primary)
        map[ORIGIN] = routeInteractor.from?.primary
        bundle.putString(DESTINATION, routeInteractor.to?.primary)
        map[DESTINATION] = routeInteractor.to?.primary

        bundle.putString(TRAVEL_CLASS, transportTypes?.filter { it.checked }?.joinToString())
        map[TRAVEL_CLASS] = transportTypes?.filter { it.checked }?.joinToString()

        analytics.logEvent(EVENT_ADD_TO_CART, bundle, map)
    }
}
