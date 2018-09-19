package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.kg.gettransfer.presentation.model.CarrierTripModel

@StateStrategyType(OneExecutionStateStrategy::class)
interface CarrierTripsView: BaseView {
    fun setTrips(trips: List<CarrierTripModel>)
}