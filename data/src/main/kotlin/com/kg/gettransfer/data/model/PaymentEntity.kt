package com.kg.gettransfer.data.model

data class PaymentEntity(
    val type: String,
    val url: String?,
    val id: Long?
) {

    companion object {
        const val TYPE       = "type"
        const val URL        = "url"
        const val PAYMENT_ID = "payment_id"
    }
}
