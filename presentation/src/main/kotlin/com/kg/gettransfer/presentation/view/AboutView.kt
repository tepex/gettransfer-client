package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface AboutView : BaseView {
    fun finish()

    companion object {
        val EXTRA_OPEN_MAIN = "${AboutView::class.java.name}.openNewMain"
    }
}
