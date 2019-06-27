package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.*
import com.google.android.gms.maps.model.LatLng
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.model.TransferModel

@StateStrategyType(OneExecutionStateStrategy::class)
interface RatingLastTripView: BaseView {

    fun setupReviewForLastTrip(transfer: TransferModel, startPoint: LatLng, routeModel: RouteModel?)

    fun cancelReview()

    fun thanksForRate()

    fun showDetailedReview(rate: Float, offerId: Long)
}