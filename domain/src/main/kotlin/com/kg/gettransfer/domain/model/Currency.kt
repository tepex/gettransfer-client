package com.kg.gettransfer.domain.model

data class Currency(
    val code: String,
    val symbol: String
) {

    override fun hashCode() = code.hashCode()

    override fun equals(other: Any?) = code.equals(other)

    companion object {
        val POPULAR = arrayOf("USD", "EUR", "GBP", "RUB")
    }
}
