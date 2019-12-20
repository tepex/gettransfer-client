package com.kg.gettransfer.domain.model

data class CheckoutcomTokenRequest(
    val type: String,
    val number: String,
    val month: String,
    val year: String,
    val cvv: String
) {
    companion object {
        const val CARD = "card"
    }
}
