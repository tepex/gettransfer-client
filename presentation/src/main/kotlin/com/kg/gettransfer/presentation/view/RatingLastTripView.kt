package com.kg.gettransfer.presentation.view

import com.google.android.gms.maps.model.LatLng
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.model.TransferModel

interface RatingLastTripView: BaseView {
    fun setupReviewForLastTrip(transfer: TransferModel, startPoint: LatLng, vehicle: String, color: String, routeModel: RouteModel?)
    fun cancelReview()
    fun askRateInPlayMarket()
    fun thanksForRate()
    fun showDetailedReview(rate: Float, offerId: Long)
}