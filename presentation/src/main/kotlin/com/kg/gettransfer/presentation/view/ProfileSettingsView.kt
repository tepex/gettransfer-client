package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.kg.gettransfer.presentation.model.ProfileModel

@StateStrategyType(OneExecutionStateStrategy::class)
interface ProfileSettingsView : BaseView {
    fun initFields(profile: ProfileModel)
    fun setEnabledBtnSave(enabled: Boolean)
    fun setEnabledPhoneField(enabled: Boolean)
}