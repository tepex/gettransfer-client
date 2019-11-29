package com.kg.gettransfer.data.model

import com.google.gson.JsonObject
import com.kg.gettransfer.domain.model.PaymentProcessRequest

data class PaymentProcessRequestEntity(
    val paymentId: Long,
    val token: TokenEntity<String, JsonObject>
) {
    companion object {
        const val PAYMENT_ID = "payment_id"
        const val TOKEN      = "token"
    }
}

fun PaymentProcessRequest.map() =
    PaymentProcessRequestEntity(
        paymentId,
        token.map()
    )
