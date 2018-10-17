package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.OfferRemote
import com.kg.gettransfer.data.OfferDataStore

/**
 * Implementation of the [RemoteDataStore] interface to provide a means of communicating with the remote data source
 */
open class OfferRemoteDataStore(private val remote: OfferRemote): OfferDataStore {
    override suspend fun getOffers(id: Long) = remote.getOffers(id)
}
