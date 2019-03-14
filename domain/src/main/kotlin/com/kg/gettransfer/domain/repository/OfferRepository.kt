package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Result

interface OfferRepository {
    suspend fun getOffers(id: Long): Result<List<Offer>>
    fun newOffer(offer: Offer): Result<Offer>
    fun clearOffersCache()
}
