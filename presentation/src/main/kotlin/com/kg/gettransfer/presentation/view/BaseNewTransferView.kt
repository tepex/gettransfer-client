package com.kg.gettransfer.presentation.view

import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@Suppress("TooManyFunctions")
@StateStrategyType(OneExecutionStateStrategy::class)
interface BaseNewTransferView : MvpView, BaseNetworkWarning {
    fun goToSearchAddress(isClickTo: Boolean)
    fun goToCreateOrder()
}
