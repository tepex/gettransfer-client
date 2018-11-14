package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.PromoDiscountEntity

interface PromoRemote {
    suspend fun getDiscount(code: String): PromoDiscountEntity
}