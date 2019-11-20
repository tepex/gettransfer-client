package com.kg.gettransfer.presentation.presenter

import com.google.android.gms.maps.model.LatLng
import com.kg.gettransfer.domain.model.RouteInfoRequest
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.presentation.mapper.RouteMapper
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.ui.SystemUtils
import com.kg.gettransfer.presentation.view.BaseMapDialogView
import org.koin.core.inject

abstract class BaseMapDialogPresenter<BV : BaseMapDialogView> : BasePresenter<BV>() {

    private val routeMapper: RouteMapper by inject()

    private var mapIsReady = false
    var transfer: Transfer? = null
    var routeModel: RouteModel? = null

    fun onMapReady() {
        mapIsReady = true
        if (transfer != null) setRoute()
    }

    open suspend fun setupReview(transfer: Transfer) {
        this.transfer = transfer
        routeModel = if (transfer.to != null) createRouteModel(transfer) else null
        if (mapIsReady) setRoute()
    }

    fun setRoute() {
        transfer?.let { transfer ->
            transfer.from.point?.let { fromPoint ->
                viewState.setRoute(
                    routeModel,
                    transfer.from.name,
                    LatLng(fromPoint.latitude, fromPoint.longitude)
                )
            }
        }
    }

    private suspend fun createRouteModel(transfer: Transfer): RouteModel? {
        val route = transfer.from.point?.let { from ->
            transfer.to?.point?.let { to ->
                orderInteractor.getRouteInfo(
                    RouteInfoRequest(
                        from = from,
                        to = to,
                        hourlyDuration = transfer.duration,
                        withPrices = false,
                        returnWay = false,
                        currency = sessionInteractor.currency.code,
                        dateTime = null
                    )
                ).model
            }
        }

        return transfer.from.point?.let { fromPoint ->
            transfer.to?.point?.let { toPoint ->
                val isRoundTrip = transfer.dateReturnLocal != null
                routeMapper.getView(
                    transfer.from.name,
                    transfer.to?.name,
                    fromPoint,
                    toPoint,
                    SystemUtils.formatDateTime(transfer.dateToLocal),
                    transfer.distance,
                    isRoundTrip,
                    route?.polyLines
                )
            }
        }
    }
}
