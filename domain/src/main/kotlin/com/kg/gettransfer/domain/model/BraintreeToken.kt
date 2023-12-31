package com.kg.gettransfer.domain.model

data class BraintreeToken(
    val token: String,
    val environment: String
) {

    companion object {
        val EMPTY = BraintreeToken("", "")
    }
}
