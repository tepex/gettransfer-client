package com.kg.gettransfer.domain.model

data class Payment(
    val type: Type,
    val url: String?,
    val id: Long?,
    val params: Params?
) {

    enum class Type { IFRAME, WIDGET, NOTHING }

    companion object {
        val EMPTY = Payment(Type.NOTHING, null, null, null)
    }
}

data class Params(
    val amount: String,
    val currency: String,
    val paymentId: Long
)
