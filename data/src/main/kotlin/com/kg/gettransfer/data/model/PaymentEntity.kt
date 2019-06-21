package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.Params
import com.kg.gettransfer.domain.model.Payment

data class PaymentEntity(
    val type: String,
    val url: String?,
    val id: Long?,
    val params: ParamsEntity?
) {

    companion object {
        const val TYPE       = "type"
        const val URL        = "url"
        const val PAYMENT_ID = "payment_id"
        const val PARAMS     = "params"
    }
}

data class ParamsEntity(
    val amount: String,
    val currency: String,
    val paymentId: Long
) {

    companion object {
        const val AMOUNT     = "amount"
        const val CURRENCY   = "currency"
        const val PAYMENT_ID = "payment_id"
    }
}

fun ParamsEntity.map() = Params(amount, currency, paymentId)

fun PaymentEntity.map() =
    Payment(
        Payment.Type.values().find { it.name.toLowerCase() == type } ?: Payment.Type.NOTHING,
        url,
        id,
        params?.let { it.map() }
    )
