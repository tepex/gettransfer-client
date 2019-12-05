package com.kg.gettransfer.presentation.view

import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

import com.kg.gettransfer.presentation.model.TransferModel

@StateStrategyType(OneExecutionStateStrategy::class)
interface RequestsFragmentView : BaseView {
    fun updateTransfers(transfers: List<TransferModel>, pagesCount: Int?)
    fun updateCardWithDriverCoordinates(transferId: Long)
    fun updateEvents(eventsCount: Map<Long, Int>)
    fun onEmptyList()
    fun removeTransfers()
}
