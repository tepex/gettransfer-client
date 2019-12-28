package com.kg.gettransfer.presentation.presenter

import moxy.InjectViewState

import com.kg.gettransfer.domain.model.ReviewRate
import com.kg.gettransfer.presentation.view.RatingDetailView
import com.kg.gettransfer.utilities.Analytics

@InjectViewState
class RatingDetailPresenter : BasePresenter<RatingDetailView>() {

    internal var offerId
        get() = reviewInteractor.offerRateID
        set(value) { reviewInteractor.offerRateID = value }

    internal var vehicleRating: Float?
        get() = reviewInteractor.vehicleRating
        set(value) { reviewInteractor.vehicleRating = value }

    internal var driverRating: Float?
        get() = reviewInteractor.driverRating
        set(value) { reviewInteractor.driverRating = value }

    internal var communicationRating: Float?
        get() = reviewInteractor.communicationRating
        set(value) { reviewInteractor.communicationRating = value }

    internal var comment: String
        get() = reviewInteractor.comment
        set(value) { reviewInteractor.comment = value }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        initRatingFields()
    }

    private fun initRatingFields() {
        vehicleRating?.let { viewState.setRatingVehicle(it) }
        driverRating?.let { viewState.setRatingDriver(it) }
        communicationRating?.let { viewState.setRatingPunctuality(it) }
        viewState.setDividersVisibility()
        if (comment.isNotEmpty()) viewState.showComment(comment)
        ratingChanged()
    }

    fun onCommonRatingChanged(rating: Float) {
        updateSecondaryRatings(rating)
    }

    private fun updateSecondaryRatings(rating: Float) {
        if (vehicleRating != null) {
            vehicleRating = rating
            viewState.setRatingVehicle(rating)
        }
        if (driverRating != null) {
            driverRating = rating
            viewState.setRatingDriver(rating)
        }
        if (communicationRating != null) {
            communicationRating = rating
            viewState.setRatingPunctuality(rating)
        }
    }

    fun ratingChanged() {
        listOfNotNull(vehicleRating, driverRating, communicationRating).let { list ->
            if (list.isNotEmpty()) {
                viewState.setRatingCommon(list.sum() / list.size)
            }
        }
    }

    fun onClickSend() = utils.launchSuspend {
        val listRates = reviewInteractor.createListOfDetailedRates()
        utils.asyncAwait { reviewInteractor.sendRates(false) }
        utils.asyncAwait { reviewInteractor.pushComment() }
        viewState.exitAndReportSuccess(listRates, comment)
        logAverageRate(listRates.map { it.rateValue }.average())
        logDetailRate(listRates, comment)
    }

    fun onClickComment(comment: String) {
        viewState.showCommentDialog(comment)
    }

    private fun logAverageRate(rate: Double) =
        analytics.logEvent(Analytics.EVENT_REVIEW_AVERAGE, Analytics.REVIEW, rate)

    private fun logDetailRate(list: List<ReviewRate>, comment: String) {
        val values = mutableListOf<Pair<String, Any>>()
        list.forEach { reviewRate ->
            values.add(analytics.reviewDetailKey(reviewRate.rateType.name) to reviewRate.rateValue)
        }
        values.add(Analytics.REVIEW_COMMENT to comment)
        analytics.logEvent(Analytics.EVENT_TRANSFER_REVIEW_DETAILED, values)
    }
}
