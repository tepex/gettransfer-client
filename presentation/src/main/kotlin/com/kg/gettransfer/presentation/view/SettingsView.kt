package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.kg.gettransfer.domain.model.Profile

import com.kg.gettransfer.presentation.model.EndpointModel
import com.kg.gettransfer.presentation.model.LocaleModel

@StateStrategyType(OneExecutionStateStrategy::class)
interface SettingsView : BaseView {
    fun setLocales(locales: List<LocaleModel>)
    fun setEndpoints(endpoints: List<EndpointModel>)
    fun setDistanceUnit(inMiles: Boolean)
    fun setEmailNotifications(isLoggedIn: Boolean, enabled: Boolean)
    fun setCalendarModes(calendarModesKeys: List<String>)
    fun setDaysOfWeek(daysOfWeek: List<CharSequence>)

    fun setCurrency(currency: String)
    fun setLocale(locale: String, code: String)
    fun setCalendarMode(calendarModeKey: String)
    fun setFirstDayOfWeek(dayOfWeek: String)
    fun setEndpoint(endpoint: EndpointModel)

    fun initGeneralSettingsLayout()
    //fun initLoggedInUserSettings(profile: ProfileModel)
    fun initProfileField(isLoggedIn: Boolean, profile: Profile)
    fun initCarrierLayout()
    fun showDebugMenu()
    fun hideDebugMenu()

    fun showCurrencyChooser()
    fun restartApp()
}
