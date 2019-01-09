package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.ReviewRateEntity

import org.koin.standalone.KoinComponent

interface ReviewDataStore : KoinComponent {
    suspend fun sendReview(offerId: Long, reviewRate: ReviewRateEntity)
    suspend fun sendFeedBackComment(offerId: Long, comment: String)
}
