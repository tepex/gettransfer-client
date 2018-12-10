package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.interactor.CarrierTripInteractor
import com.kg.gettransfer.domain.interactor.RouteInteractor

import com.kg.gettransfer.presentation.mapper.CarrierTripMapper
import com.kg.gettransfer.presentation.mapper.RouteMapper

import com.kg.gettransfer.presentation.model.CarrierTripModel
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

    private var routeModel: RouteModel? = null
    private lateinit var trip: CarrierTripModel
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

                val r = utils.asyncAwait { routeInteractor.getRouteInfo(tripInfo.from.point!!, tripInfo.to.point!!, false, false) }
                if (r.error == null && r.model.success) {
                    routeModel = routeMapper.getView(
                        r.model.distance,
                        r.model.polyLines,
                        trip.from,
                        trip.to,
                        tripInfo.from.point!!,
                        tripInfo.to.point!!,
                        trip.dateTime
                    )
                    routeModel?.let { viewState.setRoute(Utils.getPolyline(it), it) }
                }
            }
            viewState.blockInterface(false)
        }
    }

    fun onCallClick() {}
}
