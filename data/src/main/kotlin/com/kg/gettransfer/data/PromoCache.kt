package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.PromoDiscountEntity

import org.koin.standalone.KoinComponent

interface PromoCache: KoinComponent {
    suspend fun getDiscount(code: String): PromoDiscountEntity
}