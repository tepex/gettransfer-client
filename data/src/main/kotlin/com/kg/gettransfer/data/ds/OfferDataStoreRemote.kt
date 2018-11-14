package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.OfferRemote
import com.kg.gettransfer.data.OfferDataStore

open class OfferDataStoreRemote(private val remote: OfferRemote): OfferDataStore {
    override suspend fun getOffers(id: Long) = remote.getOffers(id)
}
