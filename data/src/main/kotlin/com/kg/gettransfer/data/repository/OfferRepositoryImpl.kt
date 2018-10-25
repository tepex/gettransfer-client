package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.RemoteException
import com.kg.gettransfer.data.ds.OfferDataStoreFactory

import com.kg.gettransfer.data.mapper.ExceptionMapper
import com.kg.gettransfer.data.mapper.OfferMapper

import com.kg.gettransfer.data.model.OfferEntity
import com.kg.gettransfer.data.model.OfferEntityListener

import com.kg.gettransfer.domain.model.OfferListener
import com.kg.gettransfer.domain.repository.OfferRepository

class OfferRepositoryImpl(private val factory: OfferDataStoreFactory,
                          private val mapper: OfferMapper): OfferRepository, OfferEntityListener {
    private var listener: OfferListener? = null

    override suspend fun getOffers(id: Long) = 
        factory.retrieveRemoteDataStore().getOffers(id).map { mapper.fromEntity(it) }

    override fun setListener(listener: OfferListener) {
        this.listener = listener
        factory.retrieveSocketDataStore().setListener(this) 
    }
    
    override fun removeListener(listener: OfferListener) {
        this.listener = null
        factory.retrieveSocketDataStore().removeListener(this)
    }
    
    override fun onNewOffer(offer: OfferEntity) {
        listener?.let { it.onNewOffer(mapper.fromEntity(offer)) }
    }
    
    override fun onError(e: RemoteException) {
        listener?.let { it.onError(ExceptionMapper.map(e)) }
    }
}
