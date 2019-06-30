package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.OfferRemote
import com.kg.gettransfer.data.OfferDataStore
import com.kg.gettransfer.data.model.OfferEntity
import org.koin.core.inject

open class OfferDataStoreRemote : OfferDataStore {

    private val remote: OfferRemote by inject()

    override fun setOffer(offer: OfferEntity) { throw UnsupportedOperationException() }

    override fun setOffers(offers: List<OfferEntity>) { throw UnsupportedOperationException() }

    override suspend fun getOffers(id: Long) = remote.getOffers(id)

    override fun clearOffersCache() { throw UnsupportedOperationException() }
}
