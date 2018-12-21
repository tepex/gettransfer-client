package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.OfferEntity
import org.koin.standalone.KoinComponent

interface ReviewDataStore: KoinComponent {
    suspend fun sendReview(offerId: Long, mapOfRate: HashMap<String, Int>, comment: String): Any
}