package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

import com.kg.gettransfer.presentation.model.TransferModel

@StateStrategyType(OneExecutionStateStrategy::class)
interface RequestsFragmentView: BaseView {
    fun updateTransfers(transfers: List<TransferModel>)
    fun updateCardWithDriverCoordinates(transferId: Long)
    fun updateEvents(eventsCount: Map<Long, Int>)
    fun onEmptyList()
}
