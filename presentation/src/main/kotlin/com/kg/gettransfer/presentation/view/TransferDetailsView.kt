package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.TransferModel

@StateStrategyType(OneExecutionStateStrategy::class)
interface TransferDetailsView: BaseView, RouteView {
    companion object {
        val EXTRA_TRANSFER_ID = "${TransferDetailsView::class.java.name}.transferId"
    }

    fun setTransfer(transferModel: TransferModel)
    fun setOffer(offerModel: OfferModel)
    fun setButtonCancelVisible(visible: Boolean)
    fun setPinHourlyTransfer(placeName: String, info: String, point: LatLng)
}
