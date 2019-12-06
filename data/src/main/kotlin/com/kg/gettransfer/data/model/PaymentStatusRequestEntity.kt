package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.PaymentStatusRequest

data class PaymentStatusRequestEntity(
    val paymentId: Long,
    val isSuccess: Boolean,
    val failureDescription: String?
) {

    companion object {
        const val STATUS_SUCCESSFUL = "successful"
        const val STATUS_FAILED     = "failed"
    }
}

fun PaymentStatusRequest.map() = PaymentStatusRequestEntity(paymentId, isSuccess, failureDescription)
