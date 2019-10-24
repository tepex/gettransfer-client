package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.OfferFeedbackEntity
import com.kg.gettransfer.data.model.OfferRateEntity

import org.koin.core.KoinComponent

interface ReviewCache : KoinComponent {

    suspend fun insertRate(offerRate: OfferRateEntity)

    suspend fun getAllRates(): List<OfferRateEntity>

    suspend fun deleteRate(rateId: Long)

    suspend fun insertFeedback(offerFeedback: OfferFeedbackEntity)

    suspend fun getAllFeedbacks(): List<OfferFeedbackEntity>

    suspend fun deleteOfferFeedback(offerId: Long)

    suspend fun deleteReviews()
}
