package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.OfferSocket
import com.kg.gettransfer.data.RemoteException

import com.kg.gettransfer.data.ds.OfferDataStoreFactory

import com.kg.gettransfer.data.mapper.ExceptionMapper
import com.kg.gettransfer.data.mapper.OfferMapper

import com.kg.gettransfer.data.model.OfferEntity

import com.kg.gettransfer.domain.repository.OfferRepository

import org.slf4j.LoggerFactory

class OfferRepositoryImpl(private val factory: OfferDataStoreFactory,
                          private val mapper: OfferMapper): OfferRepository, OfferSocket {
    companion object {
        @JvmField val TAG = "GTR-data"
        private val log = LoggerFactory.getLogger(TAG)
    }
    
    override suspend fun getOffers(id: Long) = 
        factory.retrieveRemoteDataStore().getOffers(id).map { mapper.fromEntity(it) }
    
    override fun onNewOffer(offer: OfferEntity) {
        log.debug("onNewOffer: $offer")
    }
    
    override fun onError(e: RemoteException) {
        log.error("WebSocket error", e)
    }
}
