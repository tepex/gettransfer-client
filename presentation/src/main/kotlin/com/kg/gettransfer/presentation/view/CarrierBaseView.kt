package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface CarrierBaseView: BaseView {
    fun openCoordinateService()
    fun stopCoordinateService()

    companion object {
        const val SERVICE_ACTION = "com.kg.gettransfer.service.CoordinateService"
        const val PACKAGE        = "com.kg.gettransfer"
    }
}