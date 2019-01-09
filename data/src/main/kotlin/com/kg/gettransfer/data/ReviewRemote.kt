package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.ReviewRateEntity

import org.koin.standalone.KoinComponent

interface ReviewRemote : KoinComponent {
    suspend fun sendReview(id: Long, reviewRate: ReviewRateEntity)
    suspend fun sendFeedBackComment(id: Long, comment: String)
}
