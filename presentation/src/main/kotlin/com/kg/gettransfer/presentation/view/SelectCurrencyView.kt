package com.kg.gettransfer.presentation.view

import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

import com.kg.gettransfer.presentation.model.CurrencyModel

@StateStrategyType(OneExecutionStateStrategy::class)
interface SelectCurrencyView : BaseView, BaseBottomSheetView {
    fun setCurrencies(all: List<CurrencyModel>, selected: CurrencyModel)
    fun setPopularCurrencies(popular: List<CurrencyModel>, selected: CurrencyModel)
    fun currencyChanged(currency: CurrencyModel)
}
