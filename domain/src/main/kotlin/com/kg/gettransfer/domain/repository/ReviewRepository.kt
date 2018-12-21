package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.Transfer

interface ReviewRepository {
    suspend fun rateTrip(offerId: Long, map: HashMap<String, Int>, comment: String): Result<Any>
}