package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.repository.ReviewRepository
import com.kg.gettransfer.domain.model.Result

class ReviewInteractor(private val repository: ReviewRepository) {
    var isReviewSuggested: Boolean = false
    private set

    var offerIdForReview: Long = 0
    var shouldAskRateInMarket: Boolean = false

    fun rateCanceled() { isReviewSuggested = true }

    suspend fun sendRates(rateMap: HashMap<String, Int>, comment: String): Result<Any> {
        isReviewSuggested = true
        val response = repository.rateTrip(offerIdForReview, rateMap, comment)
        offerIdForReview = 0
        return response
    }

    suspend fun sendTopRate () = sendRates(hashMapOf(VEHICLE to MAX_RATE, DRIVER to MAX_RATE, PUNCTUALITY to MAX_RATE), EMPTY_COMMENT)

    companion object {
        const val MAX_RATE = 5
        const val APP_RATED_IN_MARKET = -1

        const val VEHICLE       = "vehicle"
        const val DRIVER        = "driver"
        const val PUNCTUALITY   = "fair"

        const val EMPTY_COMMENT = ""
    }
}