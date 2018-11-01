package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Offer

interface OfferRepository {
    suspend fun getOffers(id: Long): List<Offer>
}
