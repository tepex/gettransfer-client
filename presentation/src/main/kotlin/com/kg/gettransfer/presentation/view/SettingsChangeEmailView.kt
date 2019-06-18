package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface SettingsChangeEmailView: BaseView {
    fun setToolbar(email: String?)
    fun setEnabledBtnChangeEmail(enable: Boolean)
    fun showCodeLayout()
    fun setTimer(resendDelay: Long)
    fun setWrongCodeError()
}