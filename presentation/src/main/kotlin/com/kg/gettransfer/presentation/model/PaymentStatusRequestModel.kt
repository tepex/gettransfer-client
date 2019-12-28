package com.kg.gettransfer.presentation.model

data class PaymentStatusRequestModel(
    val paymentId: Long,
    val isSuccess: Boolean,
    val failureDescription: String?
)
