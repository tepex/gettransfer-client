package com.kg.gettransfer.domain.model

data class PaymentStatusRequest(
    val paymentId: Long?,
    val pgOrderId: Long?,
    val withoutRedirect: Boolean?,
    val success: Boolean
)
