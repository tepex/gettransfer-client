package com.kg.gettransfer.presentation.model

import com.kg.gettransfer.domain.model.Currency

class CurrencyModel(val delegate: Currency) : CharSequence {
    val code = delegate.code
    val symbol = delegate.symbol
    /*val symbol: String
        *//* Dirty hack for ruble, Yuan ¥, THB ฿ *//*
        get() = when(code) {
            "RUB" -> "\u20BD"
            "CNY" -> "¥"
            "THB" -> "฿"
            else  -> delegate.symbol
        }*/
    var nameWithoutSymbol: String = java.util.Currency.getInstance(delegate.code).displayName
    var name = "$nameWithoutSymbol $symbol"
    override val length = name.length

    override fun toString(): String = name

    override operator fun get(index: Int): Char = name.get(index)

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence = name.subSequence(startIndex, endIndex)
}

fun Currency.map() = CurrencyModel(this)
