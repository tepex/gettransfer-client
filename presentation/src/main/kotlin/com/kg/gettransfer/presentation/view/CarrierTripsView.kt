package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

import com.kg.gettransfer.presentation.model.CarrierTripsRVItemModel
import com.kg.gettransfer.presentation.model.ProfileModel

@StateStrategyType(OneExecutionStateStrategy::class)
interface CarrierTripsView : BaseView {
    fun setTrips(tripsItems: List<CarrierTripsRVItemModel>, startTodayPosition: Int, endTodayPosition: Int)
    fun initNavigation(profile: ProfileModel)
    fun showReadMoreDialog()
}
