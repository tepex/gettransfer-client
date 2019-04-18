package com.kg.gettransfer.presentation

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.kg.gettransfer.presentation.view.BaseView

@StateStrategyType(OneExecutionStateStrategy::class)
interface ChildSeatsView: BaseView {

    companion object {
        const val INFANT      = 1
        const val CONVERTIBLE = 2
        const val BOOSTER     = 3
    }
}