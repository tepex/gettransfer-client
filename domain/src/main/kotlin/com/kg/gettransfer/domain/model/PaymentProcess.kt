package com.kg.gettransfer.domain.model

data class PaymentProcess(
    val payment: PaymentStatus,
    val redirect: String?
) {

    companion object {
        val EMPTY = PaymentProcess(PaymentStatus.EMPTY, null)
    }
}
