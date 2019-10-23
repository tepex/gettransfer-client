package com.kg.gettransfer.presentation.view

import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

import com.kg.gettransfer.presentation.model.LocaleModel

@StateStrategyType(OneExecutionStateStrategy::class)
interface SelectLanguageView : BaseView, BaseBottomSheetView {
    fun setLanguages(all: List<LocaleModel>, selected: LocaleModel)
    fun recreateActivity()
}
