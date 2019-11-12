package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.domain.model.RouteInfoRequest
import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.presentation.mapper.RouteMapper
import com.kg.gettransfer.presentation.model.map

import com.kg.gettransfer.presentation.ui.SystemUtils
import com.kg.gettransfer.presentation.ui.Utils

import com.kg.gettransfer.presentation.view.PaymentSuccessfulView
import com.kg.gettransfer.presentation.view.Screens

import org.koin.core.inject

@InjectViewState
class PaymentSuccessfulPresenter : BasePresenter<PaymentSuccessfulView>() {

    private val routeMapper: RouteMapper by inject()

    internal var offerId = 0L
    internal var transferId = 0L
    private var phoneToCall: String? = null

    fun setMapRoute() {
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val result = utils.asyncAwait { transferInteractor.getTransfer(transferId) }
            val transfer = result.model
            val transferModel = transfer.map(configsManager.getConfigs().transportTypes.map { it.map() })
            if (result.error != null && !result.fromCache) {
                viewState.setError(result.error!!)
            } else {
                if (transfer.to != null) {
                    val r = utils.asyncAwait {
                        orderInteractor.getRouteInfo(
                            RouteInfoRequest(
                                transfer.from.point!!,
                                transfer.to!!.point!!,
                                false,
                                false,
                                sessionInteractor.currency.code,
                                null
                            )
                        )
                    }
                    r.cacheError?.let { viewState.setError(it) }
                    if (r.error == null || (r.error != null && r.fromCache)) {
                        val routeModel = routeMapper.getView(
                            r.model.distance,
                            r.model.polyLines,
                            transfer.from.name,
                            transfer.to!!.name,
                            transfer.from.point!!,
                            transfer.to!!.point!!,
                            SystemUtils.formatDateTime(transferModel.dateTime)
                        )
                        viewState.setRoute(Utils.getPolyline(routeModel))
                    }
                } else {
                    if (transfer.duration != null) {
                        setHourlyTransfer(transfer)
                    }
                }
                val (days, hours, minutes) = Utils.convertDuration(transferModel.timeToTransfer)
                viewState.setRemainTime(days, hours, minutes)
            }
            utils.asyncAwait { offerInteractor.getOffers(transfer.id) }
            phoneToCall = offerInteractor.getOffer(offerId)?.phoneToCall
            if (offerId != 0L) {
                viewState.initChatButton()
                if (phoneToCall != null) viewState.initCallButton()
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
        phoneToCall?.let { callPhone(it) }
    }

    fun onChatClick() {
        router.replaceScreen(Screens.Chat(transferId))
    }

    fun onDetailsClick() {
        router.replaceScreen(Screens.Details(transferId))
    }

    fun onDownloadVoucherClick() = downloadManager.downloadVoucher(transferId)
}
