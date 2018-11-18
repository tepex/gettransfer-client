package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

import com.kg.gettransfer.presentation.model.CarrierTripModel
import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.model.RouteModel

@StateStrategyType(OneExecutionStateStrategy::class)
interface CarrierTripDetailsView: BaseView {
    companion object {
        val EXTRA_TRIP_ID = "${CarrierTripDetailsView::class.java.name}.tripId"
    }
    
    //fun setRoute(routeModel: RouteModel)
    fun setRoute(polyline: PolylineModel, routeModel: RouteModel)
    fun setTripInfo(trip: CarrierTripModel)
}