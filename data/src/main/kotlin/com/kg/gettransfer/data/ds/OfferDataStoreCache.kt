package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.OfferCache
import com.kg.gettransfer.data.OfferDataStore
import com.kg.gettransfer.data.model.OfferEntity
import org.koin.core.inject

open class OfferDataStoreCache : OfferDataStore {
    private val cache: OfferCache by inject()

    override fun setOffer(offer: OfferEntity) = cache.setOffer(offer)
    override fun setOffers(transferId: Long, offers: List<OfferEntity>) = cache.setOffers(transferId, offers)

    override suspend fun getOffers(id: Long) = cache.getOffers(id)

    override fun clearOffersCache() { cache.deleteAllOffers() }
}
