package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.MvpView

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.TransferModel

@StateStrategyType(OneExecutionStateStrategy::class)
interface OffersView: BaseView {
    fun setTransfer(transferModel: TransferModel)
    fun setDate(date: String)
    fun setOffers(offers: List<OfferModel>)
    fun setSortState(sortCategory: String, sortHigherToLower: Boolean)
    fun showAlertCancelRequest()
    fun showBottomSheetOfferDetails(offer: OfferModel)
    fun addNewOffer(offer: OfferModel)
}
