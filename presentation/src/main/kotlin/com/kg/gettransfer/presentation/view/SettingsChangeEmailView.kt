package com.kg.gettransfer.presentation.view

import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface SettingsChangeEmailView : BaseView {
    fun setToolbar(email: String?)
    fun showCodeLayout()
    fun setTimer(resendDelay: Long)
    fun setWrongCodeError(details: String)
}
