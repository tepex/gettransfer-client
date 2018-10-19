package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.repository.PromoRepository

class PromoInteractor(val repository: PromoRepository) {
    suspend fun getDiscountByPromo(promoCode: String) = repository.getDiscount(promoCode)
}