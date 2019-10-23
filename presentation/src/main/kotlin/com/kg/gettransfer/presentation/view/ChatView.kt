package com.kg.gettransfer.presentation.view

import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import com.kg.gettransfer.presentation.model.ChatModel
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.TransferModel

@StateStrategyType(OneExecutionStateStrategy::class)
interface ChatView : BaseView {
    fun setToolbar(transfer: TransferModel, offer: OfferModel?)
    fun setChat(chat: ChatModel)
    fun scrollToEnd()
    fun notifyData()

    companion object {
        val EXTRA_TRANSFER_ID = "${ChatView::class.java.name}.transferId"
    }
}