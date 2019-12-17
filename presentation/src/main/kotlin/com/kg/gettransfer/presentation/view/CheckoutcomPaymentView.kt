package com.kg.gettransfer.presentation.view

import com.checkout.android_sdk.Utils.Environment
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface CheckoutcomPaymentView : BaseView {
    fun setPrice(price: String)
    fun initPaymentForm(environment: Environment, publicKey: String)

    fun setCardNumberLength(length: Int)
    fun setCVCLength(length: Int)
    fun setCardTypeIcon(iconResId: Int)

    fun generateToken(number: String, name: String, month: String, year: String, cvc: String)
    fun redirectTo3ds(redirectUrl: String)

    fun highLightErrorField(fields: List<Int>)

    companion object {
        val EXTRA_PAYMENT_ID = "${CheckoutcomPaymentView::class.java.name}.paymentId"
    }
}
