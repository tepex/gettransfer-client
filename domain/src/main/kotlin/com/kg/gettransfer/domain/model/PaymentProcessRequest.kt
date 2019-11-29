package com.kg.gettransfer.domain.model

import com.google.gson.JsonObject

data class PaymentProcessRequest(
    val paymentId: Long,
    val token: Token<String, JsonObject>
)
