package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.ds.OfferDataStoreFactory

import com.kg.gettransfer.data.mapper.OfferMapper

import com.kg.gettransfer.domain.OfferListener
import com.kg.gettransfer.domain.repository.OfferRepository

class OfferRepositoryImpl(private val factory: OfferDataStoreFactory,
                          private val mapper: OfferMapper): OfferRepository {
    override suspend fun getOffers(id: Long) = 
        factory.retrieveRemoteDataStore().getOffers(id).map { mapper.fromEntity(it) }

    override fun setListener(listener: OfferListener) {
        factory.retrieveSocketDataStore().connect(listener, 
    }
    
    override fun removeListener(listener: OfferListener) { repository.removeListener(listener) }
}
