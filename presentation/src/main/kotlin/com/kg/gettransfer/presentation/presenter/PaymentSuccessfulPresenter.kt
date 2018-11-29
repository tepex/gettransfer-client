package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.domain.interactor.OfferInteractor
import com.kg.gettransfer.domain.interactor.RouteInteractor
import com.kg.gettransfer.domain.interactor.TransferInteractor
import com.kg.gettransfer.presentation.model.Mappers
import com.kg.gettransfer.presentation.model.TransferModel
import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.view.PaymentSuccessfulView
import com.kg.gettransfer.presentation.view.Screens
import org.koin.standalone.inject

@InjectViewState
class PaymentSuccessfulPresenter : BasePresenter<PaymentSuccessfulView>() {

    private val offerInteractor: OfferInteractor by inject()
    private val transferInteractor: TransferInteractor by inject()
    private val routeInteractor: RouteInteractor by inject()

    internal var offerId = 0L
    internal var transferId = 0L

    private lateinit var transferModel: TransferModel

    fun onCallClick() {
        viewState.call(offerInteractor.getOffer(offerId)?.carrier?.profile?.phone)
    }

    fun onDetailsClick() {
        router.navigateTo(Screens.Details(transferId))
    }

    fun setMapRoute() {
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val result = utils.asyncAwait { transferInteractor.getTransfer(transferId) }
            if(result.error != null) viewState.setError(result.error!!)
            else {
                transferModel = Mappers.getTransferModel(result.model,
                        systemInteractor.locale,
                        systemInteractor.distanceUnit,
                        systemInteractor.transportTypes)

                if(result.model.to != null) {
                    val r = utils.asyncAwait { routeInteractor
                            .getRouteInfo(result.model.from.point!!, result.model.to!!.point!!, false, false) }
                    if(r.error == null) {
                        val routeModel = Mappers.getRouteModel(r.model.distance,
                                systemInteractor.distanceUnit,
                                r.model.polyLines,
                                result.model.from.name!!,
                                result.model.to!!.name!!,
                                result.model.from.point!!,
                                result.model.to!!.point!!,
                                transferModel.dateTime)
                        viewState.setRoute(Utils.getPolyline(routeModel))
                    }
                }
            }
            viewState.blockInterface(false)
        }
    }
}