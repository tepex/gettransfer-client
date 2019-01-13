package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.OfferCache
import com.kg.gettransfer.data.OfferDataStore

import com.kg.gettransfer.data.model.OfferEntity

import org.koin.standalone.inject

open class OfferDataStoreCache: OfferDataStore {
    private val cache: OfferCache by inject()

    override fun setOffer(offer: OfferEntity) = cache.setOffer(offer)
    override fun setOffers(offers: List<OfferEntity>) = cache.setOffers(offers)
    
    override suspend fun getOffers(id: Long) = cache.getOffers(id)

    override fun clearOffersCache() { cache.deleteAllOffers() }
}
