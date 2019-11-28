package com.kg.gettransfer.presentation.view

import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface PlatronPaymentView: BaseView {

    companion object {
        val EXTRA_URL = "${PlatronPaymentView::class.java.name}.url"
    }
}
