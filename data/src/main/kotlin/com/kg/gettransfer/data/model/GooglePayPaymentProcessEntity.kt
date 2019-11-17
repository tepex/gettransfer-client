package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.GooglePayPaymentProcess

data class GooglePayPaymentProcessEntity(
    val paymentId: String,
    val token: String
) {
    companion object {
        const val PAYMENT_ID = "payment_id"
        const val TOKEN = "token"
    }
}

fun GooglePayPaymentProcess.map() =
    GooglePayPaymentProcessEntity(
        paymentId,
        token
    )