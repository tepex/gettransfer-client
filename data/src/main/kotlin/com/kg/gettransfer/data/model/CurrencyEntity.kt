package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.Currency

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrencyEntity(
    @SerialName(ISO_CODE) val code: String,
    @SerialName(SYMBOL) val symbol: String
) {

    companion object {
        const val ISO_CODE = "iso_code"
        const val SYMBOL   = "symbol"
    }
}

fun CurrencyEntity.map() = Currency(code, symbol)
