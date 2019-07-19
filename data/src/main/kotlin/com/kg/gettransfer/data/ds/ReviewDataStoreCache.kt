package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.ReviewDataStore
import com.kg.gettransfer.data.model.ReviewRateEntity
import com.kg.gettransfer.data.ReviewCache
import com.kg.gettransfer.data.model.OfferFeedbackEntity
import com.kg.gettransfer.data.model.OfferRateEntity

import org.koin.core.inject

import java.lang.UnsupportedOperationException

class ReviewDataStoreCache : ReviewDataStore {

    private val cache: ReviewCache by inject()

    override suspend fun saveRate(offerRate: OfferRateEntity) = cache.insertRate(offerRate)

    override suspend fun getAllRates() = cache.getAllRates()

    override suspend fun deleteRate(rateId: Long) = cache.deleteRate(rateId)


    override suspend fun saveFeedback(offerFeedback: OfferFeedbackEntity) = cache.insertFeedback(offerFeedback)

    override suspend fun getAllFeedbacks() = cache.getAllFeedbacks()

    override suspend fun deleteOfferFeedback(offerId: Long) = cache.deleteOfferFeedback(offerId)


    override suspend fun deleteReviews() = cache.deleteReviews()


    override suspend fun sendReview(offerId: Long, reviewRate: ReviewRateEntity) =
        throw UnsupportedOperationException()

    override suspend fun sendFeedBackComment(offerId: Long, comment: String) =
        throw UnsupportedOperationException()
}