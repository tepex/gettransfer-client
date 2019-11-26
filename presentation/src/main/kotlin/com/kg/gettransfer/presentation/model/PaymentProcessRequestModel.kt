package com.kg.gettransfer.presentation.model

data class PaymentProcessRequestModel(
    val paymentId: Long,
    val token: String,
    val isStringToken: Boolean
)