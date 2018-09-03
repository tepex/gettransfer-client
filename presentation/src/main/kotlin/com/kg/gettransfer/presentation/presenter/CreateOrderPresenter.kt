package com.kg.gettransfer.presentation.presenter

import android.content.res.Resources

import android.support.annotation.CallSuper

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

import com.kg.gettransfer.presentation.view.CreateOrderView

import kotlinx.coroutines.experimental.Job

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
    
    private var passengers: Int = MIN_PASSENGERS
    private var children: Int = MIN_CHILDREN
    var flightNumber: String? = null
    
    companion object {
        @JvmField val MIN_PASSENGERS = 1
        @JvmField val MIN_CHILDREN = 0
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
            }

            viewState.setTransportTypes(configs.transportTypes, routeInfo.prices!!)
            viewState.setCurrencies(configs.currencies)
            viewState.setMapInfo(routeInfo, addressInteractor.route, configs.distanceUnits.get(0))
        }, { e ->
            Timber.e(e)
            //viewState.setError(R.string.err_address_service_xxx, false)
        }, { /* viewState.blockInterface(false) */ })
    }
	
    fun changePassengers(count: Int) {
        passengers += count
        if(passengers < MIN_PASSENGERS) passengers = MIN_PASSENGERS
        viewState.setPassengers(passengers)
    }

    fun changeChildren(count: Int) {
        children += count
        if(children < MIN_CHILDREN) children = MIN_CHILDREN
        viewState.setChildren(children)
    }
    
    fun changeDateTimeTransfer(year: Int, month: Int, day: Int, hour: Int, minute: Int) {
        val dateTimeString = StringBuilder()
        dateTimeString.append(day).append(" ").append(resources.getStringArray(R.array.months_name)[month])
                .append(" ").append(year).append(", ").append(hour).append(":").append(minute)
        viewState.setDateTimeTransfer(dateTimeString.toString())
    }
    
    fun changeCurrency(selected: Int) {
        viewState.setCurrency(configs.currencies.get(selected).symbol)
    }

    fun setComment(comment: String) { viewState.setComment(comment) }
    fun showLicenceAgreement() { router.navigateTo(Screens.LICENCE_AGREE) }

    fun onGetTransferClick() {
        viewState.blockInterface(true)
        val from = addressInteractor.route.first
        val to = addressInteractor.route.second
        val trip = Trip(Date(), viewState.getFlightNumber())
        /* filter */
        val transportTypes = configs.transportTypes
        
        Timber.d("from: %s", from)
        Timber.d("to: %s", to)
        Timber.d("trip: %s", trip)
        Timber.d("transport types: %s", transportTypes)
        Timber.d("passengers: %d", passengers)
        Timber.d("children: %d", children)
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
