package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.ReviewDataStore
import com.kg.gettransfer.data.ReviewRemote
import com.kg.gettransfer.data.model.ReviewRateEntity
import com.kg.gettransfer.data.model.OfferFeedbackEntity
import com.kg.gettransfer.data.model.OfferRateEntity

import org.koin.core.inject

class ReviewDataStoreRemote : ReviewDataStore {

    private val remote: ReviewRemote by inject()

    override suspend fun sendReview(offerId: Long, reviewRate: ReviewRateEntity) =
        remote.sendReview(offerId, reviewRate)

    override suspend fun sendFeedBackComment(offerId: Long, comment: String) =
        remote.sendFeedBackComment(offerId, comment)

    override suspend fun saveRate(offerRate: OfferRateEntity) =
        throw UnsupportedOperationException()

    override suspend fun getAllRates() =
        throw UnsupportedOperationException()

    override suspend fun deleteRate(rateId: Long) =
        throw UnsupportedOperationException()

    override suspend fun saveFeedback(offerFeedback: OfferFeedbackEntity) =
        throw UnsupportedOperationException()

    override suspend fun getAllFeedbacks() =
        throw UnsupportedOperationException()

    override suspend fun deleteOfferFeedback(offerId: Long) =
        throw UnsupportedOperationException()

    override suspend fun deleteReviews() =
        throw UnsupportedOperationException()
}
