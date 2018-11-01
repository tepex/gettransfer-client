package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.OfferEntity

interface OfferDataStore {
    suspend fun getOffers(id: Long): List<OfferEntity>
}
