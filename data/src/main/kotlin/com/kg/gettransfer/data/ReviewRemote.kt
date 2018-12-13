package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.OfferEntity
import org.koin.standalone.KoinComponent

interface ReviewRemote: KoinComponent {
    suspend fun sendReview(id: Long, map: HashMap<String, Int>, comment: String): Any
}