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
import com.kg.gettransfer.domain.model.RouteInfo

import com.kg.gettransfer.presentation.Screens

import com.kg.gettransfer.presentation.model.ConfigsModel

import com.kg.gettransfer.presentation.view.CreateOrderView

import kotlinx.coroutines.experimental.Job

import ru.terrakok.cicerone.Router

import timber.log.Timber
import java.time.LocalDateTime
import java.util.*

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
	
    fun changeCounter(counterTextView: TextView, num: Int) {
        var counter = counterTextView.text.toString().toInt()
        var minCounter = 0
        if (counterTextView.id == R.id.tvCountPerson) minCounter = 1
        if (counter + num >= minCounter) counter += num
        viewState.setCounters(counterTextView, counter)
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

    fun onGetTransferClick() {router.navigateTo(Screens.OFFERS)}
    
    fun onBackCommandClick() { viewState.finish() }
    
	@CallSuper
	override fun onDestroy() {
		compositeDisposable.cancel()
		super.onDestroy()
	}
}
