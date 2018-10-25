package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.OfferListener

import com.kg.gettransfer.domain.model.Offer

interface OfferRepository {
    suspend fun getOffers(id: Long): List<Offer>
    fun setListener(listener: OfferListener)
    fun removeListener(listener: OfferListener)
}
