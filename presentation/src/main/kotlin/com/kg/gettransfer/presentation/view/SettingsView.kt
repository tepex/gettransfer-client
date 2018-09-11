package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.model.DistanceUnitModel
import com.kg.gettransfer.presentation.model.LocaleModel

@StateStrategyType(OneExecutionStateStrategy::class)
interface SettingsView: BaseView {
    fun setCurrencies(currencies: List<CurrencyModel>)
    fun setLocales(locales: List<LocaleModel>)
    fun setDistanceUnits(distanceUnits: List<DistanceUnitModel>)
    
    fun setCurrency(currency: String)
    fun setLocale(locale: String)
    fun setDistanceUnit(distanceUnit: String)
}
