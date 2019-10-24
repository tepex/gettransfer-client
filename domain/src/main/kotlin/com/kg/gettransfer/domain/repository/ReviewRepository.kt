package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.ReviewRate

interface ReviewRepository {
    var offerRateID: Long
    var comment: String
    var rates: MutableSet<ReviewRate>

    suspend fun rateTrip(): Result<Unit>

    suspend fun pushComment(): Result<Unit>

    suspend fun checkCachedData()

    suspend fun clearReviewCache()

    fun releaseReviewData()
}
