package com.kg.gettransfer.presentation.view

import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

import com.kg.gettransfer.domain.model.Profile
import com.kg.gettransfer.sys.presentation.EndpointModel

@Suppress("TooManyFunctions")
@StateStrategyType(OneExecutionStateStrategy::class)
interface SettingsView : BaseView {
    fun initGeneralSettingsLayout()
    fun initProfileField(isLoggedIn: Boolean, profile: Profile)
    fun setEmailNotifications(enabled: Boolean)
    fun hideEmailNotifications()
    fun showDebugMenu()
    fun hideDebugMenu()

    fun setEndpoints(endpoints: List<EndpointModel>)

    fun setCurrency(currency: String)
    fun setLocale(locale: String, code: String)
    fun setEndpoint(endpoint: EndpointModel)
    fun setDistanceUnit(inMiles: Boolean)

    fun showCurrencyChooser()
    fun showLanguageChooser()

    fun hideSomeDividers()
    fun recreate()
    fun setBalance(balance: String?)
    fun hideBalance()
    fun setCreditLimit(limit: String?)
    fun hideCreditLimit()
    fun showOrderItem()
}
