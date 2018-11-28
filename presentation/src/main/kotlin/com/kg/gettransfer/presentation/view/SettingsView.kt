package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.model.DistanceUnitModel
import com.kg.gettransfer.presentation.model.EndpointModel
import com.kg.gettransfer.presentation.model.LocaleModel
import java.io.File

@StateStrategyType(OneExecutionStateStrategy::class)
interface SettingsView: BaseView {
    fun setCurrencies(currencies: List<CurrencyModel>)
    fun setLocales(locales: List<LocaleModel>)
    fun setDistanceUnits(distanceUnits: List<DistanceUnitModel>)
    fun setEndpoints(endpoints: List<EndpointModel>)
    
    fun setCurrency(currency: String)
    fun setLocale(locale: String)
    fun setDistanceUnit(distanceUnit: String)
    fun setEndpoint(endpoint: EndpointModel)
    fun setLogoutButtonEnabled(enabled: Boolean)
    fun sendEmailInSupport(logsFile: File)

    fun restartApp()
}
