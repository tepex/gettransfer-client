package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.ApiInteractor
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.presentation.view.OffersView
import kotlinx.coroutines.experimental.Job
import timber.log.Timber

@InjectViewState
class OffersPresenter(private val cc: CoroutineContexts,
                      private val apiInteractor: ApiInteractor): MvpPresenter<OffersView>() {

    private val compositeDisposable = Job()
    private val utils = AsyncUtils(cc)

    lateinit var allTransfers: List<Transfer>
    lateinit var transfer: Transfer

    override fun onFirstViewAttach() {
        utils.launchAsyncTryCatchFinally(compositeDisposable, {

            utils.asyncAwait {
                //val secondPoint = addressInteractor.getLatLngByPlaceId(addressInteractor.route.second.id!!)
                //configs = ConfigsModel(apiInteractor.getConfigs())
                //account = apiInteractor.getAccount()
                //routeInfo = apiInteractor.getRouteInfo(arrayOf(addressInteractor.route.first.point.toString(),
                //        secondPoint.toString()), true, false)
                allTransfers = apiInteractor.getAllTransfers()!!
                transfer = apiInteractor.getTransfer(682)!!
                println(allTransfers)
                println(transfer)
            }

            //viewState.setTransportTypes(configs.transportTypes, routeInfo.prices!!)
            //viewState.setCurrencies(configs.currencies)
            //viewState.setMapInfo(routeInfo, addressInteractor.route, configs.distanceUnits.get(0))
        }, { e ->
            Timber.e(e)
            //viewState.setError(R.string.err_address_service_xxx, false)
        }, { /* viewState.blockInterface(false) */ })

    }

    fun onBackCommandClick() {
        viewState.finish()
    }
}