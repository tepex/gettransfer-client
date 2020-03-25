package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.ReviewRate

import java.util.HashSet

interface ReviewRepository {
    var offerRateID: Long
    var comment: String
    var rates: HashSet<ReviewRate>

    suspend fun rateTrip(): Result<Unit>

    suspend fun pushComment(): Result<Unit>

    suspend fun checkCachedData()

    suspend fun clearReviewCache()

    fun releaseReviewData()
}
