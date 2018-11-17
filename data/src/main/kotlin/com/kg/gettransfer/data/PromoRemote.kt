package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.PromoDiscountEntity

import org.koin.standalone.KoinComponent

interface PromoRemote: KoinComponent {
    suspend fun getDiscount(code: String): PromoDiscountEntity
}