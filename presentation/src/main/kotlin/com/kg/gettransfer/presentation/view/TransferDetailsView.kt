package com.kg.gettransfer.presentation.view

import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.TransferModel

@Suppress("TooManyFunctions")
@StateStrategyType(OneExecutionStateStrategy::class)
interface TransferDetailsView : BaseView, RouteView, GooglePlayView {
    fun setTransfer(transfer: TransferModel)
    fun setOffer(offer: OfferModel, childSeats: Int)
    fun showCancelationReasonsList()
    fun showAlertRestoreRequest()
    fun copyField(text: String)
    fun showDetailRate()
    fun closeRateWindow()
    fun askRateInPlayMarket()
    fun thanksForRate()
    fun showCommonRating(isShow: Boolean)
    fun showYourRateMark(isShow: Boolean, averageRate: Double = 0.0)
    fun showYourDataProgress(isShow: Boolean)

    fun moveCarMarker(bearing: Float, latLon: LatLng, show: Boolean)
    fun updateCamera(latLngList: List<LatLng>)

    companion object {
        val EXTRA_TRANSFER_ID = "${TransferDetailsView::class.java.name}.transferId"
    }
}
