package com.kg.gettransfer.presentation.model

data class PaymentStatusRequestModel(
    val paymentId: Long?,
    val pgOrderId: Long?,
    val withoutRedirect: Boolean?,
    val success: Boolean
)
