package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

import com.kg.gettransfer.presentation.model.LocaleModel

@StateStrategyType(OneExecutionStateStrategy::class)
interface SelectLanguageView : BaseView {
    fun setLanguages(all: List<LocaleModel>, selected: LocaleModel)
    fun recreateActivity()
}
