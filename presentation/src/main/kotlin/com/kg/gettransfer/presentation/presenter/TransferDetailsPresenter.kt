package com.kg.gettransfer.presentation.presenter

import android.content.Context

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.interactor.OfferInteractor
import com.kg.gettransfer.domain.interactor.RouteInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.domain.interactor.TransferInteractor

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
                               private val routeInteractor: RouteInteractor,
                               private val transferInteractor: TransferInteractor,
                               private val offerInteractor: OfferInteractor): BasePresenter<TransferDetailsView>(cc, router, systemInteractor) {

    @CallSuper
    override fun attachView(view: TransferDetailsView) {
        super.attachView(view)
        utils.launchAsyncTryCatchFinally({
            viewState.blockInterface(true)
            val transfer = utils.asyncAwait{ transferInteractor.getTransfer(transferInteractor.selectedId!!) } 
            val transferModel = Mappers.getTransferModel(transfer,
                                                         systemInteractor.locale,
                                                         systemInteractor.distanceUnit,
                                                         systemInteractor.getTransportTypes())
            viewState.setTransfer(transferModel)
            
	        val routeInfo = utils.asyncAwait { routeInteractor.getRouteInfo(transfer.from.point!!, transfer.to!!.point!!, true, false) }
	        val routeModel = Mappers.getRouteModel(routeInfo.distance,
                                                   systemInteractor.distanceUnit,
                                                   routeInfo.polyLines,
                                                   transfer.from.name!!,
                                                   transfer.to!!.name!!,
                                                   transfer.from.point!!,
                                                   transfer.to!!.point!!,
                                                   transferModel.dateTime)
            val polyline = Utils.getPolyline(routeModel)
            viewState.setRoute(polyline, routeModel)
            
	        //Timber.d("offers: ${transferModel.id} status=${transferModel.status} checkOffers: ${transferModel.checkOffers}")
            if(transferModel.checkOffers) {
	            val offers = utils.asyncAwait { offerInteractor.getOffers(transfer.id) }
	            if(offers.size == 1) viewState.setOffer(Mappers.getOfferModel(offers.first()))
	        }
	    }, { e -> Timber.e(e)
	        viewState.setError(e)
        }, { viewState.blockInterface(false) })        
	}
}
