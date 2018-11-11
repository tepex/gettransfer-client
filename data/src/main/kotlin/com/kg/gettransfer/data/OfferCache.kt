package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.OfferEntity

interface OfferCache {
    suspend fun getOffers(id: Long): List<OfferEntity>
}
