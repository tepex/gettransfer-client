package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.domain.interactor.OfferInteractor
import com.kg.gettransfer.domain.interactor.RouteInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.domain.interactor.TransferInteractor

import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.presentation.model.Mappers
import com.kg.gettransfer.presentation.model.TransferModel

import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.view.TransferDetailsView

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class TransferDetailsPresenter(router: Router,
                               systemInteractor: SystemInteractor,
                               private val routeInteractor: RouteInteractor,
                               private val transferInteractor: TransferInteractor,
                               private val offerInteractor: OfferInteractor): BasePresenter<TransferDetailsView>(router, systemInteractor) {
    companion object {
        const val TRANSFER_ID = "transfer_id"
    }
    
    private lateinit var transfer: Transfer
    private lateinit var transferModel: TransferModel
    
    internal var transferId = 0L

    @CallSuper
    override fun attachView(view: TransferDetailsView) {
        super.attachView(view)
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val result = utils.asyncAwait { transferInteractor.getTransfer(transferId) }
            if(result.error != null) viewState.setError(result.error!!)
            else {
                transferModel = Mappers.getTransferModel(result.model,
                                                         systemInteractor.locale,
                                                         systemInteractor.distanceUnit,
                                                         systemInteractor.transportTypes)
                viewState.setTransfer(transferModel)
                if(transferModel.checkOffers) {
                    val r = utils.asyncAwait { offerInteractor.getOffers(transfer.id) }
                    if(r.error == null && r.model.size == 1) viewState.setOffer(Mappers.getOfferModel(r.model.first(), systemInteractor.locale))
                }

                if(result.model.to != null) {
                    val r = utils.asyncAwait { routeInteractor.getRouteInfo(result.model.from.point!!, result.model.to!!.point!!, true, false) }
                    if(r.error == null) {                   
                        val routeModel = Mappers.getRouteModel(r.model.distance,
                                                               systemInteractor.distanceUnit,
                                                               r.model.polyLines,
                                                               result.model.from.name!!,
                                                               result.model.to!!.name!!,
                                                               result.model.from.point!!,
                                                               result.model.to!!.point!!,
                                                               transferModel.dateTime)
                        val polyline = Utils.getPolyline(routeModel)
                        viewState.setRoute(polyline, routeModel)
                    }
                } else if(result.model.duration != null) {
                    viewState.setPinHourlyTransfer(transferModel.from,
                                                   transferModel.dateTime,
                                                   LatLng(result.model.from.point!!.latitude, transfer.from.point!!.longitude))
                }
            }
            viewState.blockInterface(false)
        }
	}
}
