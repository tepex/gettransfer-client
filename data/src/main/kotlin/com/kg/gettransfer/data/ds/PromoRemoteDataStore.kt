package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.PromoDataStore
import com.kg.gettransfer.data.PromoRemote
import com.kg.gettransfer.data.RemoteException

import com.kg.gettransfer.data.mapper.ExceptionMapper

import com.kg.gettransfer.data.model.PromoEntity

class PromoRemoteDataStore(private val remote: PromoRemote): PromoDataStore {
    override suspend fun getDiscount(code: String): PromoEntity {
        try { return remote.getDiscount(code) }
        catch(e: RemoteException) { throw ExceptionMapper.map(e) }
    }
}