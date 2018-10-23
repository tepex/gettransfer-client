package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.PromoEntity

interface PromoDataStore {
    suspend fun getDiscount(code: String): PromoEntity
}