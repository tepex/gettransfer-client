package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.ReviewRate

interface ReviewRepository {
    suspend fun rateTrip(offerId: Long, list: List<ReviewRate>, comment: String): Result<Unit>
    suspend fun sendComment(offerId: Long, comment: String): Result<Unit>
}