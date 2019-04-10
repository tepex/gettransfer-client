package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.kg.gettransfer.presentation.model.CurrencyModel

@StateStrategyType(OneExecutionStateStrategy::class)
interface SelectCurrencyView : BaseView {
    fun setCurrencies(all: List<CurrencyModel>, popular: List<CurrencyModel>, selected: CurrencyModel)
    fun currencyChanged()
}