package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.interactor.CarrierTripInteractor
import com.kg.gettransfer.domain.interactor.RouteInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor

import com.kg.gettransfer.presentation.model.CarrierTripModel
import com.kg.gettransfer.presentation.model.Mappers
import com.kg.gettransfer.presentation.model.RouteModel

import com.kg.gettransfer.presentation.ui.Utils

import com.kg.gettransfer.presentation.view.CarrierTripDetailsView

import ru.terrakok.cicerone.Router

@InjectViewState
class CarrierTripDetailsPresenter(cc: CoroutineContexts,
                                  router: Router,
                                  systemInteractor: SystemInteractor,
                                  private val carrierTripInteractor: CarrierTripInteractor,
                                  private val routeInteractor: RouteInteractor): BasePresenter<CarrierTripDetailsView>(cc, router, systemInteractor) {

    private var selectedTripId: Long? = null
    private lateinit var trip: CarrierTripModel
    private var routeModel: RouteModel? = null

    override fun onFirstViewAttach() {
        utils.launchSuspend {
            viewState.blockInterface(true)
            selectedTripId = carrierTripInteractor.selectedTripId
            val result = utils.asyncAwait { carrierTripInteractor.getCarrierTrip(selectedTripId!!) }
            if(result.error != null) viewState.setError(result.error!!)
            else {
                val tripInfo = result.model!!
                trip = Mappers.getCarrierTripModel(tripInfo, systemInteractor.locale, systemInteractor.distanceUnit)
                viewState.setTripInfo(trip)
            
                val routeInfo = routeInteractor.getRouteInfo(tripInfo.from.point!!, tripInfo.to.point!!, false, false)
                routeInfo?.let {
                    routeModel = Mappers.getRouteModel(it.distance,
                                                       systemInteractor.distanceUnit,
                                                       it.polyLines,
                                                       trip.from,
                                                       trip.to,
                                                       tripInfo.from.point!!,
                                                       tripInfo.to.point!!,
                                                       trip.dateTime)
                }
                routeModel?.let { viewState.setRoute(Utils.getPolyline(it), it) }
            }
            viewState.blockInterface(false)
        }
    }

    fun onCallClick() {}
}
