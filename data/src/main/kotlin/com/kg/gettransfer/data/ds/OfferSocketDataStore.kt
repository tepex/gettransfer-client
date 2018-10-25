package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.OfferSocket
import com.kg.gettransfer.data.OfferDataStore
import com.kg.gettransfer.data.RemoteException

import com.kg.gettransfer.data.mapper.ExceptionMapper

import com.kg.gettransfer.data.model.OfferEntity
import com.kg.gettransfer.data.model.OfferEntityListener

open class OfferSocketDataStore(private val socket: OfferSocket): OfferDataStore {
    override suspend fun getOffers(id: Long): List<OfferEntity> { throw UnsupportedOperationException() }
    
    override fun setListener(listener: OfferEntityListener) {
        try { return socket.setListener(listener) }
        catch(e: RemoteException) { throw ExceptionMapper.map(e) }
    }
    
    override fun removeListener(listener: OfferEntityListener) {
        try { return socket.removeListener(listener) }
        catch(e: RemoteException) { throw ExceptionMapper.map(e) }
    }
}
