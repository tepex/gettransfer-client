package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface PaymentView: BaseView {

    companion object {
        val EXTRA_URL          = "${PaymentView::class.java.name}.url"
        val EXTRA_PERCENTAGE   = "${PaymentView::class.java.name}.percentage"
        val EXTRA_PAYMENT_TYPE = "${PaymentView::class.java.name}.paymentType"
    }
}
