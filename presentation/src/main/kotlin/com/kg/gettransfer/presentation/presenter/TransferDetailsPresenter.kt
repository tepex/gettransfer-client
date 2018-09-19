package com.kg.gettransfer.presentation.presenter

import android.content.Context

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.domain.interactor.TransferInteractor

import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.presentation.model.Mappers
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.model.TransferModel

import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.view.TransferDetailsView

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class TransferDetailsPresenter(cc: CoroutineContexts,
                               router: Router,
                               systemInteractor: SystemInteractor,
                               private val transferInteractor: TransferInteractor): BasePresenter<TransferDetailsView>(cc, router, systemInteractor) {

    private var transfer: Transfer? = null
    private var routeModel: RouteModel? = null
    private var offerModel: OfferModel? = null
    private var transferModel: TransferModel? = null

    override fun onFirstViewAttach() {
        utils.launchAsyncTryCatchFinally({
            viewState.blockInterface(true)
            transfer = utils.asyncAwait { transferInteractor.getTransfer() }
            transferModel = Mappers.getTransferModel(transfer!!,
                    systemInteractor.getLocale(),
                    systemInteractor.getDistanceUnit(),
                    systemInteractor.getTransportTypes())
            viewState.setTransfer(transferModel!!)
	        if(transfer!!.checkOffers) {
	            val offers = utils.asyncAwait { transferInteractor.getOffers() }
	            if(transfer!!.offersCount!! >= 1 && offers.size >= 1) {
	                offerModel = Mappers.getOfferModel(offers.first())
	                viewState.setOffer(offerModel!!)
	            }	            
	        }
            
            val from = transfer!!.from
            val to = transfer!!.to!!
	        val routeInfo = utils.asyncAwait {
	            transferInteractor.getRouteInfo(from.point, to.point, true, false)
	        }
	        routeModel = Mappers.getRouteModel(routeInfo.distance,
                                               systemInteractor.getDistanceUnit(),
                                               routeInfo.polyLines,
                                               from.name,
                                               to.name,
                                               transferModel!!.dateTime)
	        
            viewState.setRoute(routeModel!!)
	    }, { e -> viewState.setError(false, R.string.err_server, e.message)
        }, { viewState.blockInterface(false) })        
	}
	
    @CallSuper
    override fun attachView(view: TransferDetailsView) {
        super.attachView(view)
        transfer?.let { viewState.setTransfer(Mappers.getTransferModel(it,
                                                       systemInteractor.getLocale(),
                                                       systemInteractor.getDistanceUnit(),
                                                       systemInteractor.getTransportTypes()))
        }
        routeModel?.let { viewState.setRoute(it) }
        offerModel?.let { viewState.setOffer(it) }
    }
}
