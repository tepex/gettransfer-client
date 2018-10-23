package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.PromoEntity

interface PromoRemote {
    suspend fun getDiscount(code: String): PromoEntity
}