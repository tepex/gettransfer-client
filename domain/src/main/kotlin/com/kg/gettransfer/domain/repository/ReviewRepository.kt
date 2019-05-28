package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.ReviewRate

interface ReviewRepository {
    var currentOfferRateID: Long
    var currentComment: String
    var rates: MutableSet<ReviewRate>
    suspend fun rateTrip(): Result<Unit>
    suspend fun sendComment(offerId: Long, comment: String): Result<Unit>
    suspend fun pushComment(): Result<Unit>
    suspend fun pushTopRates(): Result<Unit>
    fun releaseUserData()
}