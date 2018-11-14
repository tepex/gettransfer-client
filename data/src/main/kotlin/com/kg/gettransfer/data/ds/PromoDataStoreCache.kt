package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.PromoDataStore
import com.kg.gettransfer.data.PromoCache
import com.kg.gettransfer.data.RemoteException

import com.kg.gettransfer.data.mapper.ExceptionMapper

import com.kg.gettransfer.data.model.PromoDiscountEntity

class PromoDataStoreCache(/*private val remote: PromoCache*/): PromoDataStore {
    override suspend fun getDiscount(code: String): PromoDiscountEntity {
        throw UnsupportedOperationException()
    }
}