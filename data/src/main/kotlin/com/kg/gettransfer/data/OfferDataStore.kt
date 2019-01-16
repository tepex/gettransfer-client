package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.OfferEntity

import org.koin.standalone.KoinComponent

interface OfferDataStore: KoinComponent {
    fun setOffers(offers: List<OfferEntity>)
    fun setOffer(offer: OfferEntity)
    suspend fun getOffers(id: Long): List<OfferEntity>
    fun clearOffersCache()
}
