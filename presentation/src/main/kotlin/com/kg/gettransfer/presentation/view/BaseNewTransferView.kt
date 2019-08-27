package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@Suppress("TooManyFunctions")
@StateStrategyType(OneExecutionStateStrategy::class)
interface BaseNewTransferView : MvpView, BaseNetworkWarning {
    fun defineAddressRetrieving(block: (withGps: Boolean) -> Unit)
    fun goToSearchAddress(isClickTo: Boolean, isCameFromMap: Boolean)
    fun goToCreateOrder()
}
