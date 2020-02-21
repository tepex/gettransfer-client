package com.kg.gettransfer.presentation.view

import androidx.annotation.DrawableRes

import com.checkout.android_sdk.Utils.CardUtils.Cards

import com.kg.gettransfer.R

import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface CheckoutcomPaymentView : BaseView {

    fun setCardNumberLength(length: Int)
    fun setCVCLength(length: Int)
    fun setCardTypeIcon(cardType: Cards)

    fun redirectTo3ds(redirectUrl: String)

    fun highLightErrorField(fields: List<Int>)

    companion object {
        val EXTRA_PAYMENT_ID = "${CheckoutcomPaymentView::class.java.name}.paymentId"
        val EXTRA_AMOUNT_FORMATTED = "${CheckoutcomPaymentView::class.java.name}.amountFormatted"
        val CARD_MAP = mapOf(
            Cards.AMEX       to R.drawable.ic_amex,
            Cards.DISCOVER   to R.drawable.ic_discover,
            Cards.MAESTRO    to R.drawable.ic_maestro,
            Cards.MASTERCARD to R.drawable.ic_master_card,
            Cards.VISA       to R.drawable.ic_visa_2
        )
    }
}
