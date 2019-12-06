package com.kg.gettransfer.presentation.view

import com.checkout.android_sdk.Utils.Environment
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface CheckoutcomPaymentView: BaseView {
    fun initPaymentForm(environment: Environment, publicKey: String)
    fun redirectTo3ds(redirectUrl: String)
    fun clearForm()

    companion object {
        val EXTRA_PAYMENT_ID = "${CheckoutcomPaymentView::class.java.name}.paymentId"
    }
}