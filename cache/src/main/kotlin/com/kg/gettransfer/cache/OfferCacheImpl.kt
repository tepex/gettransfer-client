package com.kg.gettransfer.cache

import com.kg.gettransfer.cache.model.map
import com.kg.gettransfer.data.OfferCache
import com.kg.gettransfer.data.model.OfferEntity
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class OfferCacheImpl : OfferCache, KoinComponent {

    private val db: CacheDatabase by inject()

    override fun setOffers(offers: List<OfferEntity>) =
        db.offersCacheDao().updateOffersForTransfer(offers.map { it.map() })

    override fun setOffer(offer: OfferEntity) = db.offersCacheDao().insertOffer(offer.map())

    override fun getOffers(id: Long) = db.offersCacheDao().getOffers(id).map { it.map() }

    override fun deleteAllOffers() = db.offersCacheDao().deleteAll()
}
