package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import android.content.Context

import android.util.Patterns

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.interactor.AddressInteractor
import com.kg.gettransfer.domain.interactor.ApiInteractor

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.Trip
import com.kg.gettransfer.domain.model.RouteInfo

import com.kg.gettransfer.presentation.Screens

import com.kg.gettransfer.presentation.model.ConfigsModel
import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.view.CreateOrderView

import java.text.DateFormat
import java.text.SimpleDateFormat

import java.util.Currency
import java.util.Date
import java.util.Locale

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class CreateOrderPresenter(cc: CoroutineContexts,
                           router: Router,
                           apiInteractor: ApiInteractor,
                           private val addressInteractor: AddressInteractor): BasePresenter<CreateOrderView>(cc, router, apiInteractor) {

    private lateinit var routeInfo: RouteInfo
    private lateinit var dateTimeFormat: DateFormat
    
    private var passengers: Int = MIN_PASSENGERS
    private var children: Int = MIN_CHILDREN
    
    var cost: Int? = null
    var date: Date = Date()
        set(value) {
            field = value
            viewState.setDateTimeTransfer(dateTimeFormat.format(date))
        }
    private var flightNumber: String? = null
    private var comment: String? = null
    private var agreeLicence = false
    
    companion object {
        @JvmField val MIN_PASSENGERS    = 1
        @JvmField val MIN_CHILDREN      = 0
    }
    
    @CallSuper
    override fun attachView(view: CreateOrderView) {
        super.attachView(view)
        utils.launchAsyncTryCatchFinally(compositeDisposable, {
            viewState.blockInterface(false)
            utils.asyncAwait {
                configs = ConfigsModel(apiInteractor.getConfigs())
                account = apiInteractor.getAccount()
                val secondPoint = addressInteractor.getLatLngByPlaceId(addressInteractor.route.second.id!!)
                routeInfo = apiInteractor.getRouteInfo(arrayOf(addressInteractor.route.first.point.toString(),
                            secondPoint.toString()), true, false)
            }
            Timber.d("account: $account")
            if(account.locale == null) account.locale = Locale.getDefault()
            if(account.currency == null) account.currency = Currency.getInstance(account.locale)
            viewState.setAccount(account)
            
            dateTimeFormat = SimpleDateFormat(Utils.DATE_TIME_PATTERN, account.locale)
            
            viewState.setTransportTypes(configs.transportTypes, routeInfo.prices!!)
            viewState.setCurrencies(configs.currencies)
            for((i, item) in configs.currencies.withIndex()) {
                if(item.code == account.currency!!.currencyCode) {
                    changeCurrency(i)
                    break
                }
            }
            date = Date()
            val distance = (view as Context).getString(R.string.distance, routeInfo.distance, account.distanceUnit)
            viewState.setRouteInfo(distance, routeInfo.polyLines, addressInteractor.route)
        }, { e ->
                if(e is ApiException) viewState.setError(false, R.string.err_server_code, e.code.toString(), e.details)
                else viewState.setError(false, R.string.err_server, e.message)
        }, { viewState.blockInterface(false) })
    }

    fun changeCurrency(selected: Int) {
        viewState.setCurrency(configs.currencies.get(selected).symbol)
    }
    
    fun changePassengers(count: Int) {
        passengers += count
        if(passengers < MIN_PASSENGERS) passengers = MIN_PASSENGERS
        viewState.setPassengers(passengers)
    }
    
    fun setName(name: String) {
        if(name.isEmpty()) account.fullName = null else account.fullName = name
        checkFields()
    }
    
    fun setEmail(email: String) {
        if(email.isEmpty()) account.email = null else account.email = email
        checkFields()
    }
    
    fun setPhone(phone: String) {
        if(phone.isEmpty()) account.phone = phone else account.phone = phone
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
        this.agreeLicence = agreeLicence
        checkFields()
    }
    
    fun showLicenceAgreement() { router.navigateTo(Screens.LICENCE_AGREE) }

    fun onGetTransferClick() {
        viewState.blockInterface(true)
        val from = addressInteractor.route.first
        val to = addressInteractor.route.second
        val trip = Trip(date, flightNumber)
        /* filter */
        val transportTypes = configs.transportTypes.filter { it.checked }.map { it.delegate.id }
        
        Timber.d("from: %s", from)
        Timber.d("to: %s, %s", to, to.point)
        Timber.d("trip: %s", trip)
        Timber.d("transport types: %s", transportTypes)
        Timber.d("===========")
        Timber.d("passenger price: $cost")
        Timber.d("date: $date")
        Timber.d("passengers: $passengers")
        Timber.d("===========")
        Timber.d("name: ${account.fullName}")
        Timber.d("email: ${account.email}")
        Timber.d("phone: ${account.phone}")
        Timber.d("children: $children")
        Timber.d("flightNumber: $flightNumber")
        Timber.d("comment: $comment")
        Timber.d("agree: $agreeLicence")
        Timber.d("account: %s", account)

        utils.launchAsyncTryCatchFinally(compositeDisposable, {
            viewState.blockInterface(true)
            val transfer = utils.asyncAwait { apiInteractor.createTransfer(from, to, trip, null, transportTypes,
                                                            passengers, children, cost, account.fullName!!, comment,
                                                            account, null, false) }
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
        val typesHasSelected = configs.transportTypes.filter { it.checked }.size > 0
        val actionEnabled = typesHasSelected &&
                            account.fullName != null &&
                            account.email != null &&
                            Patterns.EMAIL_ADDRESS.matcher(account.email!!).matches() &&
                            Utils.checkPhone(account.phone) &&
                            agreeLicence
        viewState.setGetTransferEnabled(actionEnabled)
    }
}
