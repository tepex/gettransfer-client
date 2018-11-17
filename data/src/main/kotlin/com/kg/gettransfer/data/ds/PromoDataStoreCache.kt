package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.PromoDataStore
import com.kg.gettransfer.data.PromoCache
import com.kg.gettransfer.data.RemoteException

import com.kg.gettransfer.data.mapper.ExceptionMapper

import com.kg.gettransfer.data.model.PromoDiscountEntity

import org.koin.standalone.inject

class PromoDataStoreCache: PromoDataStore {
    private val remote: PromoCache by inject()
    
    override suspend fun getDiscount(code: String): PromoDiscountEntity {
        throw UnsupportedOperationException()
    }
}