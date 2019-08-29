package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.kg.gettransfer.domain.model.Profile

import com.kg.gettransfer.sys.presentation.EndpointModel

import java.util.Locale

@StateStrategyType(OneExecutionStateStrategy::class)
interface SettingsView : BaseView {
    fun initGeneralSettingsLayout()
    fun initProfileField(isLoggedIn: Boolean, profile: Profile)
    fun setEmailNotifications(enabled: Boolean)
    fun hideEmailNotifications()
    fun initDriverLayout(isBackGroundCoordinatesAccepted: Boolean)
    fun hideDriverLayout()
    fun showDebugMenu()
    fun hideDebugMenu()

    fun setEndpoints(endpoints: List<EndpointModel>)
    fun setCalendarModes(calendarModesKeys: List<String>)
    fun setDaysOfWeek(daysOfWeek: List<CharSequence>)

    fun setCurrency(currency: String)
    fun setLocale(locale: String, code: String)
    fun setCalendarMode(calendarModeKey: String)
    fun setFirstDayOfWeek(dayOfWeek: String)
    fun setEndpoint(endpoint: EndpointModel)
    fun setDistanceUnit(inMiles: Boolean)

    fun showCurrencyChooser()
    fun showLanguageChooser()

    fun hideSomeDividers()
    fun recreate()
    fun setBalance(balance: String?)
}
