package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.RatingsModel
import com.kg.gettransfer.presentation.model.TransferModel
import java.io.InputStream

@StateStrategyType(OneExecutionStateStrategy::class)
interface TransferDetailsView: BaseView, RouteView {
    fun setTransfer(transfer: TransferModel)
    fun setOffer(offer: OfferModel, childSeats: Int)
    fun showAlertCancelRequest()
    fun copyText(text: String)
    fun showDetailRate(ratings: RatingsModel, offerId: Long, feedback: String)
    fun closeRateWindow()
    fun askRateInPlayMarket()
    fun thanksForRate()
    fun showCommonRating(isShow: Boolean)
    fun showYourRateMark(isShow: Boolean, averageRate: Double = 0.0)
    fun showYourComment(isShow: Boolean, comment: String = "")
    fun showYourDataProgress(isShow: Boolean)
    fun showCommentEditor(comment: String)
    fun showCancelRequestToast()

    fun moveCarMarker(bearing: Float, latLon: LatLng, show: Boolean)
    fun updateCamera(latLngList: List<LatLng>)

    companion object {
        val EXTRA_TRANSFER_ID = "${TransferDetailsView::class.java.name}.transferId"
    }
}
