package com.kg.gettransfer.presentation.presenter

import android.os.Bundle
import android.support.annotation.CallSuper

import android.util.Patterns

import com.arellomobile.mvp.InjectViewState
import com.google.android.gms.maps.CameraUpdate

import com.kg.gettransfer.R
import com.kg.gettransfer.R.string.from

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.model.Trip

import com.kg.gettransfer.domain.interactor.RouteInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.domain.interactor.TransferInteractor

import com.kg.gettransfer.presentation.Screens

import com.kg.gettransfer.presentation.model.Mappers
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.model.TransportTypeModel
import com.kg.gettransfer.presentation.model.UserModel

import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.view.CreateOrderView

import java.text.Format
import java.text.SimpleDateFormat

import java.util.Calendar
import java.util.Date

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class CreateOrderPresenter(cc: CoroutineContexts,
                           router: Router,
                           systemInteractor: SystemInteractor,
                           private val routeInteractor: RouteInteractor,
                           private val transferInteractor: TransferInteractor): BasePresenter<CreateOrderView>(cc, router, systemInteractor) {

    private var user: UserModel = Mappers.getUserModel(systemInteractor.account)
    private val currencies = Mappers.getCurrenciesModels(systemInteractor.getCurrencies())
    private var passengers: Int = MIN_PASSENGERS
    private var children: Int = MIN_CHILDREN
    private var dateTimeFormat: Format? = null
    private var transportTypes: List<TransportTypeModel>? = null
    private var routeModel: RouteModel? = null
    private var track: CameraUpdate? = null
    
    var cost: Int? = null
    var date: Date = Date()
        set(value) {
            field = value
            dateTimeFormat?.let { viewState.setDateTimeTransfer(it.format(date)) }
        }
    private var flightNumber: String? = null
    private var comment: String? = null
    
    companion object {
        @JvmField val MIN_PASSENGERS    = 1
        @JvmField val MIN_CHILDREN      = 0
        /* Пока сервевер не присылает минимальный временной промежуток до заказа */
        @JvmField val FUTURE_HOUR       = 6
        @JvmField val FUTURE_MINUTE     = 5

        @JvmField val PARAM_KEY = "offer_order"

    }
    
    override fun onFirstViewAttach() {
        val calendar = Calendar.getInstance(systemInteractor.locale)
        /* Server must send current locale time */
        calendar.add(Calendar.HOUR_OF_DAY, FUTURE_HOUR)
        calendar.add(Calendar.MINUTE, FUTURE_MINUTE)
        date = calendar.getTime()

        utils.launchAsyncTryCatchFinally({
            viewState.blockInterface(true)
            val from = routeInteractor.from!!
            val to = routeInteractor.to!!
	        val routeInfo = utils.asyncAwait { routeInteractor.getRouteInfo(from.point.toString(), to.point.toString(), true, false) }
	        val prices = routeInfo.prices!!.map { it.tranferId to it.min }.toMap()
            val entrance = routeInteractor.from!!.entrance
            viewState.setEntrance(entrance)
            transportTypes = Mappers.getTransportTypesModels(systemInteractor.getTransportTypes(), prices)
	        routeModel = Mappers.getRouteModel(routeInfo.distance,
                                               systemInteractor.distanceUnit,
                                               routeInfo.polyLines,
                                               from.name,
                                               to.name,
                                               SimpleDateFormat(Utils.DATE_TIME_PATTERN).format(date))

            viewState.setTransportTypes(transportTypes!!)
            val polyline = Utils.getPolyline(routeModel!!)
            track = polyline.track
            viewState.setRoute(polyline, routeModel!!)
	    }, { e -> viewState.setError(e)
        }, { viewState.blockInterface(false) })
    }
    
    @CallSuper
    override fun attachView(view: CreateOrderView) {
        super.attachView(view)
        viewState.setCurrencies(currencies)
        val i = systemInteractor.getCurrentCurrencyIndex()
        if(i != -1) changeCurrency(i)
            
        viewState.setUser(user)
        /*dateTimeFormat = SimpleDateFormat(Utils.DATE_TIME_PATTERN, systemInteractor.locale)
        viewState.setDateTimeTransfer(dateTimeFormat!!.format(date))*/
        viewState.setDateTimeTransfer(Utils.getFormatedDate(systemInteractor.locale, date))
	    transportTypes?.let { viewState.setTransportTypes(it) }
	    //routeModel?.let     { viewState.setRoute(it) }
    }

    fun changeCurrency(selected: Int) { viewState.setCurrency(currencies.get(selected).symbol) }
    
    fun changePassengers(count: Int) {
        passengers += count
        if(passengers < MIN_PASSENGERS) passengers = MIN_PASSENGERS
        viewState.setPassengers(passengers)
    }
    
    fun setName(name: String) {
        user.name = name
        checkFields()
    }
    
    fun setEmail(email: String) {
        user.email = email
        checkFields()
    }
    
    fun setPhone(phone: String) {
        user.phone = phone
        checkFields()
    }

    fun changeChildren(count: Int) {
        children += count
        if(children < MIN_CHILDREN) children = MIN_CHILDREN
        viewState.setChildren(children)
    }
    
    fun setFlightNumber(flightNumber: String) {
        if(flightNumber.isEmpty()) this.flightNumber = null else this.flightNumber = flightNumber
    }

    fun setComment(comment: String) {
        if(comment.isEmpty()) this.comment = null else this.comment = comment
        viewState.setComment(comment)
    }
    
    fun setAgreeLicence(agreeLicence: Boolean) {
        user.termsAccepted = agreeLicence
        checkFields()
    }
    
    fun showLicenceAgreement() { router.navigateTo(Screens.LICENCE_AGREE) }

    fun onGetTransferClick() {
        viewState.blockInterface(true)
        val trip = Trip(date, flightNumber)
        /* filter */
        val selectedTransportTypes = transportTypes!!.filter { it.checked }.map { it.id }
        
        Timber.d("from: %s", routeInteractor.from)
        Timber.d("to: %s", routeInteractor.to!!)
        Timber.d("trip: %s", trip)
        Timber.d("transport types: %s", selectedTransportTypes)
        Timber.d("===========")
        Timber.d("user: $user")
        Timber.d("passenger price: $cost")
        Timber.d("date: $date")
        Timber.d("passengers: $passengers")
        Timber.d("===========")
        Timber.d("children: $children")
        Timber.d("flightNumber: $flightNumber")
        Timber.d("comment: $comment")

        utils.launchAsyncTryCatchFinally({
            viewState.blockInterface(true)
            val transfer = utils.asyncAwait {
                transferInteractor.createTransfer(routeInteractor.from!!,
                                                  routeInteractor.to!!,
                                                  trip,
                                                  null,
                                                  selectedTransportTypes,
                                                  passengers,
                                                  children,
                                                  cost,
                                                  comment,
                                                  Mappers.getAccount(user),
                                                  null,
                                                  false) }
            Timber.d("new transfer: %s", transfer)
            mFBA.logEvent(USER_EVENT,createBundle(PARAM_KEY, RESULT_SUCCESS))
            router.navigateTo(Screens.OFFERS)
        }, { e ->
                if(e is ApiException) {
                    if(e.isNotLoggedIn()) {
                        login()
                    }
                    else {
                        viewState.setError(false, R.string.err_server_code, e.code.toString(), e.details)
                    }
                }
                else {
                    viewState.setError(e)
                }
            mFBA.logEvent(USER_EVENT,createBundle(PARAM_KEY, RESULT_REJECTION))
        }, { viewState.blockInterface(false) })
    }
    
    /* @TODO: Добавить реакцию на некорректное значение в поле. Отображать, где и что введено некорректно. */
    fun checkFields() {
        if(transportTypes == null) return
        val typesHasSelected = transportTypes!!.filter { it.checked }.size > 0
        val actionEnabled = typesHasSelected &&
                            !user.name.isNullOrBlank() &&
                            !user.email.isNullOrBlank() &&
                            !user.email.isNullOrBlank() &&
                            Patterns.EMAIL_ADDRESS.matcher(user.email!!).matches() &&
                            Utils.checkPhone(user.phone) &&
                            user.termsAccepted
        viewState.setGetTransferEnabled(actionEnabled)
    }

    fun onCenterRouteClick() {
        //viewState.setRoute(routeModel!!)
        viewState.centerRoute(track!!)
    }

    fun onBackClick() {
        router.navigateTo(Screens.PASSENGER_MODE)
    }

    override fun onBackCommandClick() {
        router.navigateTo(Screens.PASSENGER_MODE)
    }

}
