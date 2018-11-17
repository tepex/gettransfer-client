package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.OfferCache
import com.kg.gettransfer.data.OfferDataStore
import com.kg.gettransfer.data.RemoteException

import com.kg.gettransfer.data.mapper.ExceptionMapper

import com.kg.gettransfer.data.model.OfferEntity

import org.koin.standalone.inject

open class OfferDataStoreCache: OfferDataStore {
    private val cache: OfferCache by inject()
    
    override suspend fun getOffers(id: Long): List<OfferEntity> { throw UnsupportedOperationException() }
}
