package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.PromoDiscountEntity

interface PromoCache {
    suspend fun getDiscount(code: String): PromoDiscountEntity
}