package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.presentation.model.CurrencyModel

import java.util.Currency

open class CurrencyMapper : Mapper<CurrencyModel, Currency> {
    override fun toView(type: Currency) = CurrencyModel(type)
    override fun fromView(type: CurrencyModel): Currency { throw UnsupportedOperationException() }
}
