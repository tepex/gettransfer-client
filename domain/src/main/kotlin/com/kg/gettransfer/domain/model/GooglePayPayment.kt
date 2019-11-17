package com.kg.gettransfer.domain.model

data class GooglePayPayment(
    val paymentId: String?,
    val amount: Float?,
    val currency: String?
) {
    companion object {
        val EMPTY = GooglePayPayment(null, null, null)
    }
}