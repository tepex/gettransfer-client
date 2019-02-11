package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.kg.gettransfer.presentation.model.ProfileModel

@StateStrategyType(OneExecutionStateStrategy::class)
interface CarrierTripsMainView : CarrierBaseView{
    fun changeTypeView(type: String)
    fun initNavigation(profile: ProfileModel)
    fun showReadMoreDialog()
}