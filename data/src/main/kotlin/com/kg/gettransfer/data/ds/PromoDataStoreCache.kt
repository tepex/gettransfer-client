package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.PromoDataStore
import com.kg.gettransfer.data.model.PromoDiscountEntity

class PromoDataStoreCache : PromoDataStore {
    override suspend fun getDiscount(code: String): PromoDiscountEntity {
        throw UnsupportedOperationException()
    }
}
