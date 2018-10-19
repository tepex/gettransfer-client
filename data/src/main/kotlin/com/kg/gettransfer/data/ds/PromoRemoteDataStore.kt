package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.PromoDataStore
import com.kg.gettransfer.data.PromoRemote
import com.kg.gettransfer.data.model.PromoEntity

class PromoRemoteDataStore(private val remote: PromoRemote): PromoDataStore {
    override suspend fun getDiscount(code: String): PromoEntity = remote.getDiscount(code)
}