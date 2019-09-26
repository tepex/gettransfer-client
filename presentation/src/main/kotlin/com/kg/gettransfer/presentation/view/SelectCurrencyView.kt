package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

import com.kg.gettransfer.presentation.model.CurrencyModel

@StateStrategyType(OneExecutionStateStrategy::class)
interface SelectCurrencyView : MvpView {
    fun setCurrencies(all: List<CurrencyModel>, selected: CurrencyModel)
    fun setPopularCurrencies(popular: List<CurrencyModel>, selected: CurrencyModel)
    fun sendEvent(currency: CurrencyModel)
    fun showCurrencyFragment()
}
