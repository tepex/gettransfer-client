package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.PromoDiscountEntity
import org.koin.core.KoinComponent

interface PromoCache : KoinComponent {

    suspend fun getDiscount(code: String): PromoDiscountEntity
}
