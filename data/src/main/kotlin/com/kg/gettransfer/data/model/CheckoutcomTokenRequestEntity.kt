package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.CheckoutcomTokenRequest

data class CheckoutcomTokenRequestEntity(
    val type: String,
    val number: String,
    val month: String,
    val year: String,
    val cvv: String
) {
    companion object {
        const val TYPE         = "type"
        const val NUMBER       = "number"
        const val EXPIRY_MONTH = "expiry_month"
        const val EXPIRY_YEAR  = "expiry_year"
        const val CVV          = "cvv"
    }
}

fun CheckoutcomTokenRequest.map() =
    CheckoutcomTokenRequestEntity(
        type,
        number,
        month,
        year,
        cvv
    )
