package com.kg.gettransfer.presentation.view

import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface SettingsChangePhoneView : BaseView {
    fun setToolbar(phone: String?)
    fun showCodeLayout()
    fun setTimer(resendDelay: Long)
    fun setWrongCodeError(details: String)
}