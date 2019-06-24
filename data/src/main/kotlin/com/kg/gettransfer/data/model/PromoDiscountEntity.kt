package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.PromoDiscount

data class PromoDiscountEntity(val discountText: String)

fun PromoDiscount.map() = PromoDiscountEntity(discount)
fun PromoDiscountEntity.map() = PromoDiscount(discountText)
