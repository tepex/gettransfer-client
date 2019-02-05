package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.domain.interactor.RouteInteractor
import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.presentation.mapper.RouteMapper
import com.kg.gettransfer.presentation.mapper.TransferMapper

import com.kg.gettransfer.presentation.ui.SystemUtils
import com.kg.gettransfer.presentation.ui.Utils

import com.kg.gettransfer.presentation.view.PaymentSuccessfulView
import com.kg.gettransfer.presentation.view.Screens

import org.koin.standalone.inject

@InjectViewState
class PaymentSuccessfulPresenter : BasePresenter<PaymentSuccessfulView>() {
    private val routeInteractor: RouteInteractor by inject()

    private val routeMapper: RouteMapper by inject()
    private val transferMapper: TransferMapper by inject()

    internal var offerId = 0L
    internal var transferId = 0L

    fun setMapRoute() {
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val result = utils.asyncAwait { transferInteractor.getTransfer(transferId) }
            val transfer = result.model
            if (result.error != null) viewState.setError(result.error!!)
            else {
                if (transfer.to != null) {
                    val r = utils.asyncAwait {
                        routeInteractor
                            .getRouteInfo(transfer.from.point!!, transfer.to!!.point!!, false, false, systemInteractor.currency.currencyCode)
                    }
                    if (r.error == null) {
                        val transferModel = transferMapper.toView(transfer)
                        val (days, hours, minutes) = Utils.convertDuration(transferModel.timeToTransfer)
                        viewState.setRemainTime(days, hours, minutes)

                        val routeModel = routeMapper.getView(
                                r.model.distance,
                                r.model.polyLines,
                                transfer.from.name!!,
                                transfer.to!!.name!!,
                                transfer.from.point!!,
                                transfer.to!!.point!!,
                                SystemUtils.formatDateTime(transferModel.dateTime)
                        )
                        viewState.setRoute(Utils.getPolyline(routeModel))
                    }
                } else {
                    if (transfer.duration != null)
                        setHourlyTransfer(transfer)
                }
            }
            viewState.blockInterface(false)
        }
    }

    private fun setHourlyTransfer(transfer: Transfer) {
        val from = transfer.from.point!!
        val point = LatLng(from.latitude, from.longitude)
        viewState.setPinHourlyTransfer(point, Utils.getCameraUpdateForPin(point))
    }

    fun onCallClick() {
        offerInteractor.getOffer(offerId)?.phoneToCall?.let { callPhone(it) }
    }

    fun onDetailsClick() {
        router.navigateTo(Screens.Details(transferId))
    }
}
