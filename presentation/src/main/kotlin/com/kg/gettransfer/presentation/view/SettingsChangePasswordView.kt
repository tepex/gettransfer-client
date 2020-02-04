package com.kg.gettransfer.presentation.view

import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface SettingsChangePasswordView : BaseView {
    fun enableBtnSave(enable: Boolean)
}
