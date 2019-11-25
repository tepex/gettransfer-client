package com.kg.gettransfer.presentation.model

data class PaymentProcessModel(
    val paymentId: Long,
    val token: String,
    val isStringToken: Boolean
)