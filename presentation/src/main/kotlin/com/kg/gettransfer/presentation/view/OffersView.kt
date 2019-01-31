package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.kg.gettransfer.presentation.model.OfferItem
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.TransferModel

@StateStrategyType(OneExecutionStateStrategy::class)
interface OffersView : BaseView {
    fun setTransfer(transferModel: TransferModel)
    fun setDate(date: String)
    fun setOffers(offers: List<OfferItem>)
    fun setSortState(sortCategory: Sort, sortHigherToLower: Boolean)
    fun showAlertCancelRequest()
    fun showBottomSheetOfferDetails(offer: OfferItem)
    fun addNewOffer(offer: OfferModel)
    fun redirectView()

    companion object {
        val EXTRA_TRANSFER_ID = "${OffersView::class.java.name}.transferId"
    }

    enum class Sort {
        YEAR, RATING, PRICE;
    }
}
