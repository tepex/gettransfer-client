package com.kg.gettransfer.presentation.view

import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

import com.kg.gettransfer.presentation.model.OfferItemModel
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.TransferModel

@StateStrategyType(OneExecutionStateStrategy::class)
interface OffersView : BaseView {
    fun setTransfer(transferModel: TransferModel)
//    fun setDate(date: String)
    fun setOffers(offers: List<OfferItemModel>)
    fun setSortType(sortType: Sort, sortHigherToLower: Boolean)
    fun showBottomSheetOfferDetails(offer: OfferItemModel, isNameSignPresent: Boolean)
    fun setBannersVisible(hasOffers: Boolean)
    fun hideRefreshSpinner()
//    fun redirectView()

    companion object {
        val EXTRA_TRANSFER_ID = "${OffersView::class.java.name}.transferId"

        const val EXTRA_ORIGIN = "source"
        const val SOURCE_NOTIFICATION = "push_tap"
    }

    enum class Sort {
        YEAR, RATING, PRICE;
    }
}
