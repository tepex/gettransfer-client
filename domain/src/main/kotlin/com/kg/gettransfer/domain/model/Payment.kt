package com.kg.gettransfer.domain.model

data class Payment(
    val type: String,
    val url: String?,
    val id: Long?,
    val params: Params?
) {

    companion object {
        const val TYPE_IFRAME = "iframe"
        const val TYPE_WIDGET = "widget"
    }
}

data class Params(val amount: String,
                  val currency: String,
                  val paymentId: Long)
