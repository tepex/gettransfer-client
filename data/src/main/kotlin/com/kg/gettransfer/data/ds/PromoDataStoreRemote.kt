package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.PromoDataStore
import com.kg.gettransfer.data.PromoRemote

class PromoDataStoreRemote(private val remote: PromoRemote): PromoDataStore {
    override suspend fun getDiscount(code: String) = remote.getDiscount(code)
}
