package com.kg.gettransfer.presentation.view

import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface NewTransferMainView : BaseNewTransferView {
    fun blockFromField()
    fun blockToField()
    fun setAddressFrom(address: String)
    fun setAddressTo(address: String)
    fun selectFieldFrom()
    fun setFieldTo()
    fun setHourlyDuration(duration: Int?)
    fun updateTripView(isHourly: Boolean)
    fun showHourlyDurationDialog(durationValue: Int?)
    fun setBtnNextState(enable: Boolean)

    fun switchToMap()
    fun showReadMoreDialog()
    fun showPointB(checked: Boolean)
}
