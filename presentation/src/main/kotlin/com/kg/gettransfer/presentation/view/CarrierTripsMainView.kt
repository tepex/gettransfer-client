package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.kg.gettransfer.presentation.model.ProfileModel

@StateStrategyType(OneExecutionStateStrategy::class)
interface CarrierTripsMainView : BaseView{
    fun changeTypeView(type: String)
    fun initNavigation(profile: ProfileModel)
    fun showReadMoreDialog()
    fun askForBackGroundCoordinates()

    companion object {
        const val BG_COORDINATES_NOT_ASKED = 0
        const val BG_COORDINATES_REJECTED  = -1
    }
}