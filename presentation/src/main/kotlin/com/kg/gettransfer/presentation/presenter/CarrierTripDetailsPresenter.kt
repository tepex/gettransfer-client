package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.CarrierTripInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.domain.interactor.TransferInteractor
import com.kg.gettransfer.presentation.model.CarrierTripModel
import com.kg.gettransfer.presentation.model.Mappers
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.view.CarrierTripDetailsView
import ru.terrakok.cicerone.Router

@InjectViewState
class CarrierTripDetailsPresenter(cc: CoroutineContexts,
                                  router: Router,
                                  systemInteractor: SystemInteractor,
                                  private val carrierTripInteractor: CarrierTripInteractor,
                                  private val transferInteractor: TransferInteractor) : BasePresenter<CarrierTripDetailsView>(cc, router, systemInteractor) {

    private var selectedTripId: Long? = null
    private lateinit var trip: CarrierTripModel
    private var routeModel: RouteModel? = null

    override fun onFirstViewAttach() {
        utils.launchAsyncTryCatchFinally({
            viewState.blockInterface(true)
            selectedTripId = carrierTripInteractor.selectedTripId
            val tripInfo = utils.asyncAwait { carrierTripInteractor.getCarrierTrip(selectedTripId!!) }
            trip = Mappers.getCarrierTripModel(tripInfo,
                    systemInteractor.getLocale(),
                    systemInteractor.getDistanceUnit())

            val routeInfo = transferInteractor.getRouteInfo(tripInfo.from.point, tripInfo.to.point, false, false)
            routeModel = Mappers.getRouteModel(routeInfo.distance,
                    systemInteractor.getDistanceUnit(),
                    routeInfo.polyLines,
                    trip.from,
                    trip.to,
                    trip.dateTime)

            viewState.setTripInfo(trip)
            viewState.setRoute(routeModel!!)
        }, { e ->
            if (e is ApiException) viewState.setError(false, R.string.err_server_code, e.code.toString(), e.details)
            else viewState.setError(e)
        }, { viewState.blockInterface(false) })
    }

    fun onCallClick() {

    }
}