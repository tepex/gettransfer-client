package com.kg.gettransfer.presentation.presenter

import com.kg.gettransfer.presentation.model.CurrencyModel

interface CurrencyChangedListener {
    fun currencyChanged(currency: CurrencyModel)
}
