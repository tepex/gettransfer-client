package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.GooglePayPayment

data class GooglePayPaymentEntity(
    val paymentId: String?,
    val amount: Float?,
    val currency: String?
) {
    companion object {
        const val PARAMS = "params"
        const val PAYMENT_ID = "payment_id"
        const val AMOUNT = "amount"
        const val CURRENCY = "currency"
    }
}

fun GooglePayPaymentEntity.map() =
    GooglePayPayment(
        paymentId,
        amount,
        currency
    )