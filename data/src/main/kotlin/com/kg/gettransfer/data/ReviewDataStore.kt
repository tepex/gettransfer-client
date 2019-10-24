package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.ReviewRateEntity
import com.kg.gettransfer.data.model.OfferFeedbackEntity
import com.kg.gettransfer.data.model.OfferRateEntity

import org.koin.core.KoinComponent

interface ReviewDataStore : KoinComponent {

    suspend fun sendReview(offerId: Long, reviewRate: ReviewRateEntity)

    suspend fun sendFeedBackComment(offerId: Long, comment: String)

    suspend fun saveRate(offerRate: OfferRateEntity)

    suspend fun getAllRates(): List<OfferRateEntity>

    suspend fun deleteRate(rateId: Long)

    suspend fun saveFeedback(offerFeedback: OfferFeedbackEntity)

    suspend fun getAllFeedbacks(): List<OfferFeedbackEntity>

    suspend fun deleteOfferFeedback(offerId: Long)

    suspend fun deleteReviews()
}
