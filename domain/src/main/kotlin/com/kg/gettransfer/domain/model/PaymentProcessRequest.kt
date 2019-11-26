package com.kg.gettransfer.domain.model

data class PaymentProcessRequest(
    val paymentId: Long,
    val token: String,
    val isStringToken: Boolean
)