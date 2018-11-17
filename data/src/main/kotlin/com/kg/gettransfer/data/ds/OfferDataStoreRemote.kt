package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.OfferRemote
import com.kg.gettransfer.data.OfferDataStore

import org.koin.standalone.inject

open class OfferDataStoreRemote: OfferDataStore {
    private val remote: OfferRemote by inject()
    
    override suspend fun getOffers(id: Long) = remote.getOffers(id)
}
