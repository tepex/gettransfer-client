package com.kg.gettransfer.presentation.model

import java.util.Currency

class CurrencyModel(val delegate: Currency) : CharSequence {
    val code = delegate.currencyCode
    val symbol: String
        /* Dirty hack for ruble, Yuan ¥, THB ฿ */
        get() = when(code) {
            "RUB" -> "\u20BD"
            "CNY" -> "¥"
            "THB" -> "฿"
            else  -> delegate.symbol
        }
    val name = "${delegate.displayName} ($symbol)"
    override val length = name.length

    override fun toString(): String = name
    override operator fun get(index: Int): Char = name.get(index)
    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence = name.subSequence(startIndex, endIndex)
}
