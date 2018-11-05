package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper
import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.OfferInteractor
import com.kg.gettransfer.domain.interactor.RouteInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.domain.interactor.TransferInteractor
import com.kg.gettransfer.presentation.model.Mappers
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
            viewState.blockInterface(true, true)
            val transfer = utils.asyncAwait{ transferInteractor.getTransfer(transferInteractor.selectedId!!) } 
            val transferModel = Mappers.getTransferModel(transfer,
                                                         systemInteractor.locale,
                                                         systemInteractor.distanceUnit,
                                                         systemInteractor.transportTypes!!)
            viewState.setTransfer(transferModel)
            if(transferModel.checkOffers) {
	            val offers = utils.asyncAwait { offerInteractor.getOffers(transfer.id) }
	            if(offers.size == 1) viewState.setOffer(Mappers.getOfferModel(offers.first(), systemInteractor.locale))
	        }
            
	        val routeInfo = utils.asyncAwait { routeInteractor.getRouteInfo(transfer.from.point!!, transfer.to!!.point!!, true, false) }
	        routeInfo?.let {
	            val routeModel = Mappers.getRouteModel(it.distance,
                                                       systemInteractor.distanceUnit,
                                                       it.polyLines,
                                                       transfer.from.name!!,
                                                       transfer.to!!.name!!,
                                                       transfer.from.point!!,
                                                       transfer.to!!.point!!,
                                                       transferModel.dateTime)
                val polyline = Utils.getPolyline(routeModel)
                viewState.setRoute(polyline, routeModel)
            }
	    }, { e -> Timber.e(e)
	        viewState.setError(e)
        }, { viewState.blockInterface(false) })        
	}
}
