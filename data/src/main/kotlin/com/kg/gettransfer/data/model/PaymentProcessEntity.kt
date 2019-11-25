package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.PaymentProcess

data class PaymentProcessEntity(
    val paymentId: Long,
    val token: String,
    val isStringToken: Boolean
) {
    companion object {
        const val PAYMENT_ID = "payment_id"
        const val TOKEN      = "token"
    }
}

fun PaymentProcess.map() =
    PaymentProcessEntity(
        paymentId,
        token,
        isStringToken
    )