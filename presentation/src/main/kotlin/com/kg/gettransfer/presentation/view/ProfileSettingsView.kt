package com.kg.gettransfer.presentation.view

import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import com.kg.gettransfer.presentation.model.ProfileModel

@StateStrategyType(OneExecutionStateStrategy::class)
interface ProfileSettingsView : BaseView {
    fun initFields(profile: ProfileModel)
    fun setEnabledBtnSave(enabled: Boolean)
    fun setEnabledPhoneField(enabled: Boolean)
}