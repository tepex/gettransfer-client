package com.kg.gettransfer.cache

import com.kg.gettransfer.cache.mapper.OfferEntityMapper
import com.kg.gettransfer.data.OfferCache
import com.kg.gettransfer.data.model.OfferEntity
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class OfferCacheImpl: OfferCache, KoinComponent {
    private val db: CacheDatabase by inject()
    private val offerMapper: OfferEntityMapper by inject()

    override fun setOffers(offers: List<OfferEntity>) = db.offersCacheDao().updateOffersForTransfer(offers.map { offerMapper.toCached(it) })
    override fun setOffer(offer: OfferEntity) = db.offersCacheDao().insertOffer(offerMapper.toCached(offer))

    override fun getOffers(id: Long) = db.offersCacheDao().getOffers(id).map { offerMapper.fromCached(it) }

    override fun deleteAllOffers() = db.offersCacheDao().deleteAll()
}