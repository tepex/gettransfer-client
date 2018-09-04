package com.kg.gettransfer.presentation.presenter

import android.content.res.Resources

import android.support.annotation.CallSuper

import android.util.Patterns

import android.widget.TextView

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.AsyncUtils

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

import kotlinx.coroutines.experimental.Job

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class CreateOrderPresenter(private val resources: Resources,
                           private val cc: CoroutineContexts,
                           private val router: Router,
                           private val addressInteractor: AddressInteractor,
                           private val apiInteractor: ApiInteractor): MvpPresenter<CreateOrderView>() {

	private val compositeDisposable = Job()
	private val utils = AsyncUtils(cc)
	
	lateinit var configs: ConfigsModel
	var account: Account? = null
    lateinit var routeInfo: RouteInfo
    lateinit var dateTimeFormat: DateFormat
    
    private var passengers: Int = MIN_PASSENGERS
    private var children: Int = MIN_CHILDREN
    
    var cost: Int? = null
    var date: Date = Date()
        set(value) {
            field = value
            viewState.setDateTimeTransfer(dateTimeFormat.format(date))
        }
    private var name: String? = null
    private var email: String? = null
    private var phone: String? = null
    private var flightNumber: String? = null
    private var comment: String? = null
    private var agreeLicence = false
    
    companion object {
        @JvmField val MIN_PASSENGERS    = 1
        @JvmField val MIN_CHILDREN      = 0
        @JvmField val DATE_TIME_PATTERN = "dd MMMM yyyy, HH:mm"
    }
    
    override fun onFirstViewAttach() {
        utils.launchAsyncTryCatchFinally(compositeDisposable, {
            viewState.setRoute(addressInteractor.route)

            utils.asyncAwait {
                val secondPoint = addressInteractor.getLatLngByPlaceId(addressInteractor.route.second.id!!)
                configs = ConfigsModel(apiInteractor.getConfigs())
                account = apiInteractor.getAccount()
                routeInfo = apiInteractor.getRouteInfo(arrayOf(addressInteractor.route.first.point.toString(),
                            secondPoint.toString()), true, false)
                if(account!!.locale != null) dateTimeFormat = SimpleDateFormat(DATE_TIME_PATTERN, account!!.locale)
                else dateTimeFormat = SimpleDateFormat(DATE_TIME_PATTERN)
            }

            viewState.setTransportTypes(configs.transportTypes, routeInfo.prices!!)
            viewState.setCurrencies(configs.currencies)
            if(account?.currency != null) {
                for((i, item) in configs.currencies.withIndex()) {
                    if(item.code == account!!.currency!!.currencyCode) {
                        changeCurrency(i)
                        break
                    }
                }
            }
            date = Date()
            viewState.setMapInfo(routeInfo, addressInteractor.route, configs.distanceUnits.get(0))
        }, { e ->
            Timber.e(e)
            //viewState.setError(R.string.err_address_service_xxx, false)
        }, { /* viewState.blockInterface(false) */ })
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
        if(name.isEmpty()) this.name = null
        else this.name = name
        checkFields()
    }
    
    fun setEmail(email: String) {
        if(email.isEmpty()) this.email = null
        else this.email = email
        checkFields()
    }
    
    fun setPhone(phone: String) {
        if(phone.isEmpty()) this.phone = phone
        else this.phone = phone
        checkFields()
    }

    fun changeChildren(count: Int) {
        children += count
        if(children < MIN_CHILDREN) children = MIN_CHILDREN
        viewState.setChildren(children)
    }
    
    fun setFlightNumber(flightNumber: String) {
        if(flightNumber.isEmpty()) this.flightNumber = null
        else this.flightNumber = flightNumber
    }

    fun setComment(comment: String) {
        if(comment.isEmpty()) this.comment = null
        else this.comment = comment
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
        /* filter */
        val transportTypes = configs.transportTypes.filter { it.checked }.map { it.delegate.id }
        
        Timber.d("from: %s", from)
        Timber.d("to: %s", to)
        //Timber.d("trip: %s", trip)
        Timber.d("transport types: %s", transportTypes)
        Timber.d("===========")
        Timber.d("passenger price: $cost")
        Timber.d("date: $date")
        Timber.d("passengers: $passengers")
        Timber.d("===========")
        Timber.d("name: $name")
        Timber.d("email: $email")
        Timber.d("phone: $phone")
        Timber.d("children: $children")
        Timber.d("flightNumber: $flightNumber")
        Timber.d("comment: $comment")
        Timber.d("agree: $agreeLicence")
        /*
        utils.launchAsyncTryCatchFinally(compositeDisposable, {
            utils.asyncAwait { apiInteractor.createTransfer(
                    addressInteractor.route.first,
                    addressInteractor.route.second,
                    Trip(Date()), null,
                    
                    
            
            ) 
            router.navigateTo(Screens.OFFERS)
            }
        }, { e ->
            if(e is ApiException && e.isNotLoggedIn()) login()
            else {
                Timber.e(e)
                viewState.setError(R.string.err_server)
            }
        }, { viewState.blockInterface(false) })
        */
    }
    
    /* @TODO: Добавить реакцию на некорректное значение в поле. Отображать, где и что введено некорректно. */
    fun checkFields() {
        val typesHasSelected = configs.transportTypes.filter { it.checked }.size > 0
        val actionEnabled = typesHasSelected &&
                            name != null &&
                            email != null &&
                            Patterns.EMAIL_ADDRESS.matcher(email!!).matches() &&
                            Utils.checkPhone(phone) &&
                            agreeLicence
        viewState.setGetTransferEnabled(actionEnabled)
    }
    
    private fun login() {
        Timber.d("go to login")
        router.navigateTo(Screens.LOGIN) 
    }

    
    fun onBackCommandClick() { viewState.finish() }
    
	@CallSuper
	override fun onDestroy() {
		compositeDisposable.cancel()
		super.onDestroy()
	}
}
