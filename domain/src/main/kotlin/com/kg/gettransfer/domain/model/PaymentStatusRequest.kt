package com.kg.gettransfer.domain.model

data class PaymentStatusRequest(
    val paymentId: Long,
    val isSuccess: Boolean,
    val failureDescription: String?
)
