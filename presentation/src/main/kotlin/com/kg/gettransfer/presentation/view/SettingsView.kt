package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

import com.kg.gettransfer.presentation.model.ProfileModel
import com.kg.gettransfer.presentation.model.EndpointModel
import com.kg.gettransfer.presentation.model.LocaleModel

@StateStrategyType(OneExecutionStateStrategy::class)
interface SettingsView : BaseView {
    //fun setCurrencies(currencies: List<CurrencyModel>)
    fun setLocales(locales: List<LocaleModel>)
    //fun setDistanceUnits(distanceUnits: List<DistanceUnitModel>)
    fun setEndpoints(endpoints: List<EndpointModel>)
    fun setDistanceUnit(inMiles: Boolean)
    fun setEmailNotifications(enabled: Boolean)
    fun setCalendarModes(calendarModesKeys: List<String>)
    fun setDaysOfWeek(daysOfWeek: List<CharSequence>)

    fun setCurrency(currency: String)
    fun setLocale(locale: String, code: String)
    //fun setDistanceUnit(distanceUnit: String)
    fun setCalendarMode(calendarModeKey: String)
    fun setFirstDayOfWeek(dayOfWeek: String)
    fun setEndpoint(endpoint: EndpointModel)
    fun setLogoutButtonEnabled(enabled: Boolean)

    fun initGeneralSettingsLayout()
    fun initLoggedInUserSettings(profile: ProfileModel)
    fun initCarrierLayout()
    fun initDebugLayout()

    fun showFragment(showingView: Int)
    fun restartApp()
}
