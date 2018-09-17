package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.kg.gettransfer.domain.model.CarrierTrip
import com.kg.gettransfer.domain.model.DistanceUnit

@StateStrategyType(OneExecutionStateStrategy::class)
interface CarrierTripsView: BaseView {
    fun setTrips(trips: List<CarrierTrip>, distanceUnit: DistanceUnit)
}