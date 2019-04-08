package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.repository.ReviewRepository
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.ReviewRate

class ReviewInteractor(private val repository: ReviewRepository) {
    var isReviewSuggested = false
        private set

    var offerIdForReview = 0L
    var shouldAskRateInMarket = false

    fun rateCanceled() { isReviewSuggested = true }

    suspend fun sendRates(rateList: List<ReviewRate>, comment: String): Result<Unit> {
        isReviewSuggested = true
        val response = repository.rateTrip(offerIdForReview, rateList, comment)
        offerIdForReview = 0
        return response
    }

    suspend fun sendComment(offerId: Long, comment: String): Result<Unit> {
        return repository.sendComment(offerId, comment)
    }

    suspend fun sendTopRate() =
        sendRates(
            arrayListOf(
                ReviewRate(ReviewRate.RateType.VEHICLE,     MAX_RATE),
                ReviewRate(ReviewRate.RateType.DRIVER,      MAX_RATE),
                ReviewRate(ReviewRate.RateType.PUNCTUALITY, MAX_RATE)
            ),
            EMPTY_COMMENT)

    companion object {
        const val MAX_RATE = 5
        const val APP_RATED_IN_MARKET = -1

        const val EMPTY_COMMENT = ""
    }
}
