package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Result

interface OfferRepository {
    suspend fun getOffers(id: Long): Result<List<Offer>>
    suspend fun getOffersCached(id: Long): Result<List<Offer>>
    fun clearOffersCache()
    fun newOffer(offer: Offer)
}
