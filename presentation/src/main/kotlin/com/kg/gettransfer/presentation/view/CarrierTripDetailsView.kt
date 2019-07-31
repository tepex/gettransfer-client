package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.kg.gettransfer.presentation.model.CarrierTripModel

@StateStrategyType(OneExecutionStateStrategy::class)
interface CarrierTripDetailsView : BaseView, RouteView {
    fun setTripInfo(trip: CarrierTripModel)
    fun copyField(text: String)

    companion object {
        val EXTRA_TRIP_ID = "${CarrierTripDetailsView::class.java.name}.tripId"
        val EXTRA_TRANSFER_ID = "${CarrierTripDetailsView::class.java.name}.transferId"
    }
}
