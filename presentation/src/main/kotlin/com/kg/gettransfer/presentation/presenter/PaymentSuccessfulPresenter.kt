package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.interactor.OfferInteractor
import com.kg.gettransfer.domain.interactor.RouteInteractor
import com.kg.gettransfer.domain.interactor.TransferInteractor

import com.kg.gettransfer.presentation.mapper.RouteMapper
import com.kg.gettransfer.presentation.mapper.TransferMapper

import com.kg.gettransfer.presentation.model.TransferModel

import com.kg.gettransfer.presentation.ui.SystemUtils
import com.kg.gettransfer.presentation.ui.Utils

import com.kg.gettransfer.presentation.view.PaymentSuccessfulView
import com.kg.gettransfer.presentation.view.Screens

import org.koin.standalone.inject

@InjectViewState
class PaymentSuccessfulPresenter : BasePresenter<PaymentSuccessfulView>() {
    private val transferInteractor: TransferInteractor by inject()
    private val routeInteractor: RouteInteractor by inject()

    private val routeMapper: RouteMapper by inject()
    private val transferMapper: TransferMapper by inject()

    internal var offerId = 0L
    internal var transferId = 0L

    fun setMapRoute() {
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val result = utils.asyncAwait { transferInteractor.getTransfer(transferId) }
            if (result.error != null) viewState.setError(result.error!!)
            else {
                if (result.model.to != null) {
                    val r = utils.asyncAwait {
                        routeInteractor
                            .getRouteInfo(result.model.from.point!!, result.model.to!!.point!!, false, false, systemInteractor.currency.currencyCode)
                    }
                    if (r.error == null) {
                        val transferModel = transferMapper.toView(result.model)
                        val (days, hours, minutes) = Utils.convertDuration(transferModel.timeToTransfer)
                        viewState.setRemainTime(days, hours, minutes)

                        val routeModel = routeMapper.getView(
                            r.model.distance,
                            r.model.polyLines,
                            result.model.from.name!!,
                            result.model.to!!.name!!,
                            result.model.from.point!!,
                            result.model.to!!.point!!,
                            SystemUtils.formatDateTime(transferModel.dateTime)
                        )
                        viewState.setRoute(Utils.getPolyline(routeModel))
                    }
                }
            }
            viewState.blockInterface(false)
        }
    }

    fun onCallClick() {
        offerInteractor.getOffer(offerId)?.phoneToCall?.let { callPhone(it) }
    }

    fun onDetailsClick() {
        router.navigateTo(Screens.Details(transferId))
    }
}
