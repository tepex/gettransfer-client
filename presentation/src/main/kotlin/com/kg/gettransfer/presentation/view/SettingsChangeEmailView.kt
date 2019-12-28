package com.kg.gettransfer.presentation.view

import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface SettingsChangeEmailView: BaseView {
    fun setToolbar(email: String?)
    fun setEnabledBtnChangeEmail(enable: Boolean)
    fun showCodeLayout()
    fun setTimer(resendDelay: Long)
    fun setWrongCodeError()
}