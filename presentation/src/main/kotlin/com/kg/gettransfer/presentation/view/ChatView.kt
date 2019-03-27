package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.kg.gettransfer.presentation.model.CarrierTripModel
import com.kg.gettransfer.presentation.model.ChatModel
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.TransferModel

@StateStrategyType(OneExecutionStateStrategy::class)
interface ChatView : BaseView {
    fun setToolbar(transfer: TransferModel, offer: OfferModel?, isShowChevron: Boolean)
    fun setToolbar(carrierTrip: CarrierTripModel)
    fun setChat(chat: ChatModel)
    fun scrollToEnd()
    fun notifyData()

    companion object {
        val EXTRA_TRANSFER_ID = "${ChatView::class.java.name}.transferId"
        val EXTRA_TRIP_ID = "${ChatView::class.java.name}.tripId"
    }
}