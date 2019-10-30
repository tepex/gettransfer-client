package com.kg.gettransfer.presentation.view

import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface SelectCancelationReasonView : MvpView, BaseBottomSheetView {
    fun setCancelationReasonsList(reasons: List<Int>)
}