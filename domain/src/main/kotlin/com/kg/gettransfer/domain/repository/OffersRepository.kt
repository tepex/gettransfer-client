package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Offer

interface OffersRepository {
    suspend fun getOffers(id: Long): List<Offer>
}
