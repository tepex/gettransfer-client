package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.PromoDataStore
import com.kg.gettransfer.data.PromoRemote
import org.koin.standalone.inject

class PromoDataStoreRemote : PromoDataStore {

    private val remote: PromoRemote by inject()

    override suspend fun getDiscount(code: String) = remote.getDiscount(code)
}
