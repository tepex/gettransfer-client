package com.kg.gettransfer.presentation.view

import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface PaymentView: BaseView {

    companion object {
        val EXTRA_URL          = "${PaymentView::class.java.name}.url"
        val EXTRA_PERCENTAGE   = "${PaymentView::class.java.name}.percentage"
        val EXTRA_PAYMENT_TYPE = "${PaymentView::class.java.name}.paymentType"
    }
}
