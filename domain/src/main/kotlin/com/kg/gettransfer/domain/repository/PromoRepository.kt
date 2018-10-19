package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.PromoDiscount

interface PromoRepository {
    suspend fun getDiscount(code: String): PromoDiscount
}