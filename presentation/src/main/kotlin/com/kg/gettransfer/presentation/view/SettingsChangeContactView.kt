package com.kg.gettransfer.presentation.view

import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface SettingsChangeContactView : BaseView {
    fun setToolbar(text: String?)
    fun showCodeLayout(resendDelay: Long)
    fun hideCodeLayout()
    fun setEnabledBtnChangeContact(enable: Boolean)
    fun setWrongCodeError(details: String)
}