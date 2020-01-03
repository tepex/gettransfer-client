package com.kg.gettransfer.presentation.presenter

import moxy.InjectViewState

import com.google.android.gms.maps.model.LatLng
import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.RouteInfo

import com.kg.gettransfer.domain.model.RouteInfoRequest
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.extensions.getOffer

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
                result.error?.let { viewState.setError(it) }
            } else {
                setRoute(transfer)
                val (days, hours, minutes) = Utils.convertDuration(transferModel.timeToTransfer)
                viewState.setRemainTime(days, hours, minutes)
            }
            phoneToCall = fetchData(processError = WITHOUT_ERROR, checkLoginError = false) {
                offerInteractor.getOffers(transfer.id)
            }?.getOffer(offerId)?.phoneToCall
            if (offerId != 0L) {
                viewState.initChatButton()
                if (phoneToCall != null) viewState.initCallButton()
            }
            viewState.blockInterface(false)
        }
    }

    private suspend fun setRoute(transfer: Transfer) {
        transfer.to?.let { to ->
            transfer.from.point?.let { fromPoint ->
                to.point?.let { toPoint ->
                    setPointToPointTransfer(fromPoint, toPoint, transfer)
                }
            }
        } ?: run {
            transfer.duration?.let { setHourlyTransfer(transfer) }
        }
    }

    private suspend fun setPointToPointTransfer(fromPoint: Point, toPoint: Point, transfer: Transfer) {
        val r = getRouteInfo(fromPoint, toPoint, transfer.duration)
        r.cacheError?.let { viewState.setError(it) }
        if (r.error == null || r.error != null && r.fromCache) {
            val routeModel = routeMapper.getView(
                transfer.from.name,
                transfer.to?.name,
                fromPoint,
                toPoint,
                SystemUtils.formatDateTime(transfer.dateToLocal),
                transfer.distance,
                transfer.dateReturnLocal != null,
                r.model.polyLines
            )
            viewState.setRoute(Utils.getPolyline(routeModel))
        }
    }

    private fun setHourlyTransfer(transfer: Transfer) {
        val from = transfer.from.point
        val point = from?.let { LatLng(it.latitude, it.longitude) }
        point?.let { viewState.setPinHourlyTransfer(it, Utils.getCameraUpdateForPin(it)) }
    }

    private suspend fun getRouteInfo(fromPoint: Point, toPoint: Point, duration: Int?): Result<RouteInfo> =
        utils.asyncAwait {
            orderInteractor.getRouteInfo(
                RouteInfoRequest(
                    from = fromPoint,
                    to = toPoint,
                    hourlyDuration = duration,
                    withPrices = false,
                    returnWay = false,
                    currency = sessionInteractor.currency.code,
                    dateTo = null,
                    dateReturn = null
                )
            )
        }

    fun onCallClick() {
        phoneToCall?.let { callPhone(it) }
    }

    fun onChatClick() {
        router.navigateTo(Screens.Chat(transferId))
    }

    fun onDetailsClick() {
        router.replaceScreen(Screens.Details(transferId))
    }

    fun onDownloadVoucherClick() = downloadManager.downloadVoucher(transferId)
}
