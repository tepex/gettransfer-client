package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.Currency
import com.kg.gettransfer.presentation.model.CurrencyModel

open class CurrencyMapper : Mapper<CurrencyModel, Currency> {
    override fun toView(type: Currency) = CurrencyModel(type)
    override fun fromView(type: CurrencyModel): Currency { throw UnsupportedOperationException() }
}
