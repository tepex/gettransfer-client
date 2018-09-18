package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper
import android.support.v4.content.ContextCompat

import android.util.Patterns

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.model.Trip

import com.kg.gettransfer.domain.interactor.RouteInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.domain.interactor.TransferInteractor

import com.kg.gettransfer.presentation.Screens

import com.kg.gettransfer.presentation.model.Mappers
import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.model.TransportTypeModel

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

    private val currencies = Mappers.getCurrenciesModels(systemInteractor.getCurrencies())
    private var passengers: Int = MIN_PASSENGERS
    private var children: Int = MIN_CHILDREN
    private var dateTimeFormat: Format? = null
    private var transportTypes: List<TransportTypeModel>? = null
    private var routeModel: RouteModel? = null
    
    var cost: Int? = null
    var date: Date = Date()
        set(value) {
            field = value
            if(dateTimeFormat != null) viewState.setDateTimeTransfer(dateTimeFormat!!.format(date))
        }
    private var flightNumber: String? = null
    private var comment: String? = null
    
    companion object {
        @JvmField val MIN_PASSENGERS    = 1
        @JvmField val MIN_CHILDREN      = 0
    }
    
    override fun onFirstViewAttach() {
        utils.launchAsyncTryCatchFinally({
            viewState.blockInterface(true)
            val from = routeInteractor.from.point
            val to = routeInteractor.to!!.point
	        val routeInfo = utils.asyncAwait { transferInteractor.getRouteInfo(from, to, true, false) }
	        val prices = routeInfo.prices!!.map { it.tranferId to it.min }.toMap()
	        transportTypes = Mappers.getTransportTypesModels(systemInteractor.getTransportTypes(), prices)
	        routeModel = Mappers.getRouteModel(routeInfo.distance,
                                               systemInteractor.getDistanceUnit(),
                                               routeInfo.polyLines,
                                               from,
                                               to)
            
            viewState.setTransportTypes(transportTypes!!)
            viewState.setRoute(routeModel!!)
	    }, { e -> viewState.setError(false, R.string.err_server, e.message)
        }, { viewState.blockInterface(false) })        
        
        val calendar = Calendar.getInstance(systemInteractor.getLocale())
        /* Server must send current locale time */
        calendar.add(Calendar.HOUR_OF_DAY, 3)
        date = calendar.getTime()
    }
    
    @CallSuper
    override fun attachView(view: CreateOrderView) {
        super.attachView(view)
        viewState.setCurrencies(currencies)
        val i = systemInteractor.getCurrentCurrencyIndex()
        if(i != -1) changeCurrency(i)
            
        viewState.setAccount(systemInteractor.account)
        dateTimeFormat = SimpleDateFormat(Utils.DATE_TIME_PATTERN, systemInteractor.getLocale())
        viewState.setDateTimeTransfer(dateTimeFormat!!.format(date))
	    if(transportTypes != null) viewState.setTransportTypes(transportTypes!!)
	    if(routeModel != null) viewState.setRoute(routeModel!!)
    }

    fun changeCurrency(selected: Int) { viewState.setCurrency(currencies.get(selected).symbol) }
    
    fun changePassengers(count: Int) {
        passengers += count
        if(passengers < MIN_PASSENGERS) passengers = MIN_PASSENGERS
        viewState.setPassengers(passengers)
    }
    
    fun setName(name: String) {
        systemInteractor.account.fullName = name
        checkFields()
    }
    
    fun setEmail(email: String) {
        systemInteractor.account.email = email
        checkFields()
    }
    
    fun setPhone(phone: String) {
        systemInteractor.account.phone = phone
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
        systemInteractor.account.termsAccepted = agreeLicence
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
        Timber.d("passenger price: $cost")
        Timber.d("date: $date")
        Timber.d("passengers: $passengers")
        Timber.d("===========")
        Timber.d("account: ${systemInteractor.account}")
        Timber.d("children: $children")
        Timber.d("flightNumber: $flightNumber")
        Timber.d("comment: $comment")

        utils.launchAsyncTryCatchFinally({
            viewState.blockInterface(true)
            val transfer = utils.asyncAwait {
                transferInteractor.createTransfer(routeInteractor.from,
                                                  routeInteractor.to!!,
                                                  trip,
                                                  null,
                                                  selectedTransportTypes,
                                                  passengers,
                                                  children,
                                                  cost,
                                                  comment,
                                                  systemInteractor.account,
                                                  null,
                                                  false) }
            Timber.d("new transfer: %s", transfer)
            router.navigateTo(Screens.OFFERS)
        }, { e ->
                if(e is ApiException) {
                    if(e.isNotLoggedIn()) login()
                    else viewState.setError(false, R.string.err_server_code, e.code.toString(), e.details)
                }
                else viewState.setError(false, R.string.err_server, e.message)
        }, { viewState.blockInterface(false) })
    }
    
    /* @TODO: Добавить реакцию на некорректное значение в поле. Отображать, где и что введено некорректно. */
    fun checkFields() {
        if(transportTypes == null) return
        val typesHasSelected = transportTypes!!.filter { it.checked }.size > 0
        val actionEnabled = typesHasSelected &&
                            !systemInteractor.account.fullName.isNullOrBlank() &&
                            !systemInteractor.account.email.isNullOrBlank() &&
                            !systemInteractor.account.email.isNullOrBlank() &&
                            Patterns.EMAIL_ADDRESS.matcher(systemInteractor.account.email!!).matches() &&
                            Utils.checkPhone(systemInteractor.account.phone) &&
                            systemInteractor.account.termsAccepted
        viewState.setGetTransferEnabled(actionEnabled)
    }
}
