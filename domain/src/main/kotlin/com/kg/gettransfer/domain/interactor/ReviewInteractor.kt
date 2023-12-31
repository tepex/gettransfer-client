package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Ratings
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.ReviewRate
import com.kg.gettransfer.domain.repository.ReviewRepository

class ReviewInteractor(private val repository: ReviewRepository) {

    var isReviewSuggested = false
        private set

    var shouldAskRateInMarket = false

    var offerRateID
        get() = repository.offerRateID
        set(value) { repository.offerRateID = value }

    var comment: String
        get() = repository.comment
        set(value) { repository.comment = value }

    var rates: Set<ReviewRate>
        get() = repository.rates
        set(value) { repository.rates = value }

    var driverRating: Float? = null
    var vehicleRating: Float? = null
    var communicationRating: Float? = null

    fun setOfferReview(offer: Offer) {
        with(offer) {
            offerRateID = id
            with(ratings ?: Ratings.EMPTY) {
                driverRating = driver?.toFloat()
                vehicleRating = vehicle?.toFloat()
                communicationRating = communication?.toFloat()
            }
            comment = passengerFeedback.orEmpty()
        }
    }

    fun setRates(rate: Float) {
        if (driverRating != null) driverRating = rate
        if (vehicleRating != null) vehicleRating = rate
        if (communicationRating != null) communicationRating = rate
    }

    fun createListOfDetailedRates(): List<ReviewRate> {
        val list = arrayListOf<ReviewRate>().apply {
            driverRating?.let { add(ReviewRate(ReviewRate.RateType.DRIVER, it.toInt())) }
            communicationRating?.let { add(ReviewRate(ReviewRate.RateType.COMMUNICATION, it.toInt())) }
            vehicleRating?.let { add(ReviewRate(ReviewRate.RateType.VEHICLE, it.toInt())) }
        }
        rates = list.toSet()
        return list
    }

    fun reviewSuggested() { isReviewSuggested = true }

    suspend fun sendRates(isTopRate: Boolean = true): Result<Unit> {
        if (isTopRate) createListOfDetailedRates()
        return repository.rateTrip()
    }

    suspend fun pushComment() = repository.pushComment()

    fun releaseReviewData() = repository.releaseReviewData()

    suspend fun checkNotSendedReviews(): Result<Unit> {
        repository.checkCachedData()
        return Result(Unit)
    }

    suspend fun clearReviewCache(): Result<Unit> {
        repository.clearReviewCache()
        return Result(Unit)
    }

    companion object {
        const val MAX_RATE = 5
        const val APP_RATED_IN_MARKET = -1
    }
}
