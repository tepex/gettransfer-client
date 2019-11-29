package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.PaymentProcess

data class PaymentProcessEntity(
    val payment: PaymentStatusEntity,
    val redirect: String?
) {

    companion object {
        const val PAYMENT  = "payment"
        const val REDIRECT = "redirect"
    }
}

fun PaymentProcessEntity.map() =
    PaymentProcess(
        payment.map(),
        redirect
    )