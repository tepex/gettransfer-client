package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.OfferEntity

interface OfferRemote {
    suspend fun getOffers(id: Long): List<OfferEntity>
}
