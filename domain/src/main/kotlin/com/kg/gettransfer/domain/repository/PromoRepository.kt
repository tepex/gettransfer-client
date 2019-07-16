package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.PromoDiscount
import com.kg.gettransfer.domain.model.Result

interface PromoRepository {
    suspend fun getDiscount(code: String): Result<PromoDiscount>
}
