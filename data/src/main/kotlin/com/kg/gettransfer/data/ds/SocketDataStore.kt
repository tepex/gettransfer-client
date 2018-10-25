package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.OfferRemote
import com.kg.gettransfer.data.OfferDataStore
import com.kg.gettransfer.data.RemoteException

import com.kg.gettransfer.data.mapper.ExceptionMapper

import com.kg.gettransfer.data.model.OfferEntity

/**
 * Implementation of the [RemoteDataStore] interface to provide a means of communicating with the remote data source
 */
open class OfferRemoteDataStore(private val remote: OfferRemote): OfferDataStore {
    override suspend fun getOffers(id: Long): List<OfferEntity> {
        try { return remote.getOffers(id) }
        catch(e: RemoteException) { throw ExceptionMapper.map(e) }
    }
}
