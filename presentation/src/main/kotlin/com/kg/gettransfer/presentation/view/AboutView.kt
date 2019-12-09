package com.kg.gettransfer.presentation.view

import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface AboutView : BaseView {
    fun onBackPressed()

    companion object {
        val EXTRA_OPEN_MAIN = "${AboutView::class.java.name}.openNewMain"
    }
}
