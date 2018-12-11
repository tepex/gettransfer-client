package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.domain.interactor.CarrierTripInteractor
import com.kg.gettransfer.domain.interactor.RouteInteractor

import com.kg.gettransfer.presentation.mapper.CarrierTripMapper
import com.kg.gettransfer.presentation.mapper.RouteMapper

import com.kg.gettransfer.presentation.model.CarrierTripModel
import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.model.RouteModel

import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.view.CarrierTripDetailsView

import org.koin.standalone.inject

@InjectViewState
class CarrierTripDetailsPresenter : BasePresenter<CarrierTripDetailsView>() {

    private val carrierTripInteractor: CarrierTripInteractor by inject()
    private val routeInteractor: RouteInteractor by inject()

    private val carrierTripMapper: CarrierTripMapper by inject()
    private val routeMapper: RouteMapper by inject()

    companion object {
        @JvmField val FIELD_EMAIL = "field_email"
        @JvmField val FIELD_PHONE = "field_phone"
        @JvmField val OPERATION_COPY = "operation_copy"
        @JvmField val OPERATION_OPEN = "operation_open"
    }

    private var routeModel: RouteModel? = null
    private var polyline: PolylineModel? = null
    private var track: CameraUpdate? = null

    private lateinit var tripModel: CarrierTripModel
    internal var tripId = 0L

    override fun onFirstViewAttach() {
        utils.launchSuspend {
            viewState.blockInterface(true)
            val result = utils.asyncAwait { carrierTripInteractor.getCarrierTrip(tripId) }
            if (result.error != null) viewState.setError(result.error!!)
            else {
                val tripInfo = result.model
                trip = carrierTripMapper.toView(tripInfo)
                viewState.setTripInfo(trip)

                val r = utils.asyncAwait { routeInteractor.getRouteInfo(tripInfo.base.from.point!!, tripInfo.base.to!!.point!!, false, false) }
                if (r.error == null && r.model.success) {
                    routeModel = routeMapper.getView(
                        r.model.distance,
                        r.model.polyLines,
                        trip.base.from,
                        trip.base.to!!,
                        tripInfo.base.from.point!!,
                        tripInfo.base.to!!.point!!,
                        trip.base.dateTime
                    )
                }
            }
            viewState.blockInterface(false)
        }
    }

    fun onCenterRouteClick() { track?.let { viewState.centerRoute(it) } }

    fun makeFieldOperation(field: String, operation: String, text: String) {
        when (operation) {
            OPERATION_COPY -> viewState.copyText(text)
            OPERATION_OPEN -> {
                when (field) {
                    FIELD_PHONE -> callPhone(text)
                    FIELD_EMAIL -> sendEmail(text)
                }
            }
        }
    }
}
