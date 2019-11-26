package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.PaymentProcessRequest

data class PaymentProcessRequestEntity(
    val paymentId: Long,
    val token: String,
    val isStringToken: Boolean
) {
    companion object {
        const val PAYMENT_ID = "payment_id"
        const val TOKEN      = "token"
    }
}

fun PaymentProcessRequest.map() =
    PaymentProcessRequestEntity(
        paymentId,
        token,
        isStringToken
    )