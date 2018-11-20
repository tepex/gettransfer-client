package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import android.util.Patterns

import com.arellomobile.mvp.InjectViewState

import com.google.android.gms.maps.CameraUpdate

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.interactor.*

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

import com.yandex.metrica.YandexMetrica

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
    private var promoCode: String? = null
    
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


        /** [см. табл.][https://docs.google.com/spreadsheets/d/1RP-96GhITF8j-erfcNXQH5kM6zw17ASmnRZ96qHvkOw/edit#gid=0] */
        @JvmField val EVENT_TRANSFER = "create_transfer"
        @JvmField val EVENT_SETTINGS = "transfer_settings"

        @JvmField val PARAM_KEY_FIELD  = "field"
        @JvmField val PARAM_KEY_RESULT = "result"

        //TransferSettings Params:
        @JvmField val OFFER_PRICE_FOCUSED = "offer_price"
        @JvmField val DATE_TIME_CHANGED   = "date_time"
        @JvmField val PASSENGERS_ADDED    = "pax"
        @JvmField val FLIGHT_NUMBER_ADDED = "flight_number"
        @JvmField val CHILDREN_ADDED      = "children"
        @JvmField val COMMENT_INPUT       = "comment"

        //CreateTransfer Params:
        @JvmField val NO_TRANSPORT_SELECTED = "no_transport_type"
        @JvmField val NO_EMAIL = "invalid_email"
        @JvmField val NO_PHONE = "invalid_phone"
        @JvmField val NO_NAME  = "invalid_name"
        @JvmField val NO_LICENSE_ACCEPTED = "license_not_accepted"
        @JvmField val SERVER_ERROR = "server_error"

        //Main params:
        @JvmField val SHOW_ROUTE_CLICKED = "show_route"
        @JvmField val CAR_INFO_CLICKED   = "car_info"
        @JvmField val BACK_CLICKED       = "back"

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

    fun changeCurrency(selected: Int) { viewState.setCurrency(currencies.get(selected).symbol) }
    
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

            val logResult = utils.asyncAwait {
                systemInteractor.putAccount()
            }

            if (result.error == null && logResult.error == null) {
                router.navigateTo(Screens.Offers(result.model.id))
                logCreateTransfer(RESULT_SUCCESS)
            } else if(result.error != null) {
                when {
                    result.error!!.details == "{phone=[taken]}" -> viewState.setError(false, R.string.LNG_PHONE_TAKEN_ERROR)
                    else -> viewState.setError(result.error!!)
                }
            } else if (logResult.error != null) {
                router.navigateTo(Screens.LoginToGetOffers(result.model.id, user.profile.email))
            }
            viewState.blockInterface(false)
        }
    }

    private fun checkFieldsForRequest(): Boolean {
        var errorFiled =
            if(!Utils.checkEmail(user.profile.email))        EMAIL_FIELD
            else if(user.profile.name.isNullOrBlank())       NAME_FIELD
            else if(!Utils.checkPhone(user.profile.phone!!)) PHONE_FIELD
            else if(transportTypes != null &&
                    !transportTypes!!.any { it.checked })    TRANSPORT_FIELD
            else if(!user.termsAccepted)                     TERMS_ACCEPTED_FIELD
            else return true

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

        mFBA.logEvent(MainPresenter.EVENT_MAIN, createSingeBundle(PARAM_KEY_NAME, value))
        eventsLogger.logEvent(MainPresenter.EVENT_MAIN, createSingeBundle(PARAM_KEY_NAME, value))
        YandexMetrica.reportEvent(MainPresenter.EVENT_MAIN, map)
    }

    fun logTransferSettingsEvent(value: String) {
        val map = HashMap<String, Any>()
        map[PARAM_KEY_FIELD] = value

        mFBA.logEvent(EVENT_SETTINGS, createSingeBundle(PARAM_KEY_FIELD, value))
        eventsLogger.logEvent(EVENT_SETTINGS, createSingeBundle(PARAM_KEY_FIELD, value))
        YandexMetrica.reportEvent(EVENT_SETTINGS, map)
    }

    private fun logCreateTransfer(value: String) {
        val map = HashMap<String, Any>()
        map[PARAM_KEY_RESULT] = value

        mFBA.logEvent(EVENT_TRANSFER, createSingeBundle(PARAM_KEY_RESULT, value))
        eventsLogger.logEvent(EVENT_TRANSFER, createSingeBundle(PARAM_KEY_RESULT, value))
        YandexMetrica.reportEvent(EVENT_TRANSFER, map)
    }
}
