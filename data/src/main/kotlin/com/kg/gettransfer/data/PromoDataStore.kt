package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.PromoDiscountEntity

interface PromoDataStore {
    suspend fun getDiscount(code: String): PromoDiscountEntity
}