package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.domain.interactor.OrderInteractor
import com.kg.gettransfer.domain.model.CarrierTrip

import com.kg.gettransfer.domain.model.CarrierTripBase
import com.kg.gettransfer.domain.model.RouteInfo
import com.kg.gettransfer.domain.model.RouteInfoRequest

import com.kg.gettransfer.presentation.mapper.CarrierTripMapper
import com.kg.gettransfer.presentation.mapper.RouteMapper

import com.kg.gettransfer.presentation.model.CarrierTripModel
import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.ui.SystemUtils

import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.view.CarrierTripDetailsView
import com.kg.gettransfer.presentation.view.Screens

import org.koin.core.inject

@InjectViewState
class CarrierTripDetailsPresenter : BasePresenter<CarrierTripDetailsView>() {
    private val orderInteractor: OrderInteractor by inject()

    private val carrierTripMapper: CarrierTripMapper by inject()
    private val routeMapper: RouteMapper by inject()

    companion object {
        const val FIELD_EMAIL = "field_email"
        const val FIELD_PHONE = "field_phone"
        const val OPERATION_COPY = "operation_copy"
        const val OPERATION_OPEN = "operation_open"
    }

    private var routeModel: RouteModel? = null
    private var polyline: PolylineModel? = null
    private var track: CameraUpdate? = null

    private lateinit var tripModel: CarrierTripModel
    internal var tripId = 0L
    internal var transferId = 0L

    override fun onFirstViewAttach() {
        utils.launchSuspend {
            viewState.blockInterface(true)
            fetchData { carrierTripInteractor.getCarrierTrip(tripId) }
                    ?.let { tripInfo ->
                        setTrip(tripInfo)
                        setTripType(tripInfo.base)
                    }
            viewState.blockInterface(false)
        }
    }

    private fun setTrip(trip: CarrierTrip) {
        tripModel = carrierTripMapper.toView(trip)
        viewState.setTripInfo(tripModel)
    }

    private suspend fun setTripType(tripBase: CarrierTripBase) {
        if (tripBase.to != null && tripBase.to!!.point != null) {
            fetchData {
                orderInteractor.getRouteInfo(RouteInfoRequest(
                    tripBase.from.point!!,
                    tripBase.to!!.point!!,
                    false,
                    false,
                    sessionInteractor.currency.code,
                    null
                ))
            }?.let { setRouteTransfer(tripBase, it) }
        }
        else if (tripBase.duration != null) {
            setHourlyTransfer(tripBase)
        }
    }

    private fun setRouteTransfer(baseTrip: CarrierTripBase, route: RouteInfo) {
        routeModel = routeMapper.getView(
                route.distance,
                route.polyLines,
                baseTrip.from.name,
                baseTrip.to!!.name,
                baseTrip.from.point!!,
                baseTrip.to!!.point!!,
                SystemUtils.formatDateTime(tripModel.base.dateLocal)
        )
        routeModel?.let {
            polyline = Utils.getPolyline(it)
            track = polyline!!.track
            if (polyline!!.isVerticalRoute) viewState.setMapBottomPadding()
            viewState.setRoute(polyline!!, it)
        }
    }

    private fun setHourlyTransfer(baseTripInfo: CarrierTripBase) {
        val point = LatLng(baseTripInfo.from.point!!.latitude, baseTripInfo.from.point!!.longitude)
        track = Utils.getCameraUpdateForPin(point)
        viewState.setPinHourlyTransfer(
                tripModel.base.from,
                SystemUtils.formatDateTime(tripModel.base.dateLocal),
                point,
                track!!
        )
    }

    fun onCenterRouteClick() { track?.let { viewState.centerRoute(it) } }

    fun makeFieldOperation(field: String, operation: String, text: String) {
        when (operation) {
            OPERATION_COPY -> viewState.copyField(text)
            OPERATION_OPEN -> {
                when (field) {
                    FIELD_PHONE -> callPhone(text)
                    FIELD_EMAIL -> sendEmail(text, transferId)
                }
            }
        }
    }

    fun onChatClick(){
        router.navigateTo(Screens.Chat(transferId, tripId))
    }
}
