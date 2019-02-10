package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.ProfileModel
import com.kg.gettransfer.presentation.model.TransferModel

@StateStrategyType(OneExecutionStateStrategy::class)
interface TransferDetailsView: BaseView, RouteView {
    fun setTransfer(transfer: TransferModel, userProfile: ProfileModel, showRate: Boolean)
    fun setOffer(offer: OfferModel, childSeats: Int)
    fun showAlertCancelRequest()
    fun copyText(text: String)
    fun recreateActivity()
    fun showDetailRate(tappedRate: Float)
    fun closeRateWindow()
    fun askRateInPlayMarket()
    fun showRateInPlayMarket()
    fun thanksForRate()

    fun moveCarMarker(bearing: Float, latLon: LatLng, show: Boolean)

    companion object {
        val EXTRA_TRANSFER_ID = "${TransferDetailsView::class.java.name}.transferId"
    }
}
