package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

import com.kg.gettransfer.presentation.model.CarrierTripsRVItemModel

@StateStrategyType(OneExecutionStateStrategy::class)
interface CarrierTripsListFragmentView : BaseView {
    fun setTrips(tripsItems: List<CarrierTripsRVItemModel>, startTodayPosition: Int, endTodayPosition: Int)
}
