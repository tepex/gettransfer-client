package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface RequestsView : BaseView {
    companion object {
        const val CATEGORY_ACTIVE    = "Active"
        const val CATEGORY_ALL       = "All"
        const val CATEGORY_COMPLETED = "Completed"
    }
}
