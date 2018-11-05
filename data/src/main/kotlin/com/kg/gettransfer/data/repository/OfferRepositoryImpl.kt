package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.RemoteException

import com.kg.gettransfer.data.ds.OfferDataStoreFactory

import com.kg.gettransfer.data.mapper.ExceptionMapper
import com.kg.gettransfer.data.mapper.OfferMapper

import com.kg.gettransfer.data.model.OfferEntity

import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.repository.OfferRepository

import org.slf4j.LoggerFactory

class OfferRepositoryImpl(private val factory: OfferDataStoreFactory,
                          private val mapper: OfferMapper): OfferRepository {
    companion object {
        @JvmField val TAG = "GTR-data"
        private val log = LoggerFactory.getLogger(TAG)
    }
    
    override suspend fun getOffers(id: Long) = 
        factory.retrieveRemoteDataStore().getOffers(id).map { mapper.fromEntity(it) }
    
    override fun newOffer(offer: Offer) {
        log.debug("onNewOffer: $offer")
    }
}
