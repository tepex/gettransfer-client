package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.repository.ReviewRepository
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.ReviewRate

class ReviewInteractor(private val repository: ReviewRepository) {

    var isReviewSuggested = false
        private set

    var shouldAskRateInMarket = false

    var offerIdForReview
        get() = repository.currentOfferRateID
        set(value) { repository.currentOfferRateID = value }

    var comment: String
        get() = repository.currentComment
        set(value) { repository.currentComment = value }

    var rates: MutableSet<ReviewRate>
        get() = repository.rates
        set(value) { repository.rates = value }

    fun rateCanceled() { isReviewSuggested = true }

    suspend fun sendRates(): Result<Unit> {
        isReviewSuggested = true
        return repository.rateTrip()
    }

    suspend fun pushComment() = repository.pushComment()

    suspend fun sendTopRate() = repository.pushTopRates()

    fun releaseReviewData() = repository.releaseReviewData()

    companion object {
        const val MAX_RATE = 5
        const val APP_RATED_IN_MARKET = -1
    }
}
