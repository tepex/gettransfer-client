package com.kg.gettransfer.presentation.view

import androidx.annotation.DrawableRes
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface CheckoutcomPaymentView : BaseView {

    fun setCardNumberLength(length: Int)
    fun setCVCLength(length: Int)
    fun setCardTypeIcon(@DrawableRes iconResId: Int)

    fun redirectTo3ds(redirectUrl: String)

    fun highLightErrorField(fields: List<Int>)

    companion object {
        val EXTRA_PAYMENT_ID = "${CheckoutcomPaymentView::class.java.name}.paymentId"
        val EXTRA_AMOUNT_FORMATTED = "${CheckoutcomPaymentView::class.java.name}.amountFormatted"
    }
}
