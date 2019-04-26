package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.*
import com.google.android.gms.maps.model.LatLng
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.model.TransferModel

interface RatingLastTripView: BaseView {
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun setupReviewForLastTrip(transfer: TransferModel, startPoint: LatLng, vehicle: String, color: String, routeModel: RouteModel?)

    fun cancelReview()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun askRateInPlayMarket()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun thanksForRate()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showDetailedReview(rate: Float, offerId: Long)
}