package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.OfferDataStore
import com.kg.gettransfer.data.RemoteException

import com.kg.gettransfer.data.ds.DataStoreFactory
import com.kg.gettransfer.data.ds.OfferDataStoreCache
import com.kg.gettransfer.data.ds.OfferDataStoreRemote

import com.kg.gettransfer.data.mapper.ExceptionMapper
import com.kg.gettransfer.data.mapper.OfferMapper

import com.kg.gettransfer.data.model.OfferEntity

import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.repository.OfferRepository

class OfferRepositoryImpl(private val factory: DataStoreFactory<OfferDataStore, OfferDataStoreCache, OfferDataStoreRemote>,
                          private val mapper: OfferMapper): BaseRepository(), OfferRepository {
    override suspend fun getOffers(id: Long) = 
        factory.retrieveRemoteDataStore().getOffers(id).map { mapper.fromEntity(it) }
    
    override fun newOffer(offer: Offer) {
        log.debug("OfferRepository.newOffer: $offer")
    }
}
