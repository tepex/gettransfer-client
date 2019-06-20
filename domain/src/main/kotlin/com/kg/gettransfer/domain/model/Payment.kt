package com.kg.gettransfer.domain.model

data class Payment(
    val type: String,
    val url: String?,
    val id: Long?,
    val params: Params?
) {

    enum class Type { IFRAME, WIDGET }
    
    companion object {
        /* TODO: переделать в enum */
        const val TYPE_IFRAME = "iframe"
        const val TYPE_WIDGET = "widget"
        
        val EMPTY = Payment("", null, null, null)
    }
}

data class Params(
    val amount: String,
    val currency: String,
    val paymentId: Long
)
