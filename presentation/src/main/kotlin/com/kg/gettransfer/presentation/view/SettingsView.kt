package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface SettingsView: MvpView {
    fun finish()

    fun setSettingsCurrency(textSelectedCurrency: String)
    fun setSettingsLanguage(textSelectedLanguage: String)
    fun setSettingsDistanceUnits(textSelectedDistanceUnit: String)
}