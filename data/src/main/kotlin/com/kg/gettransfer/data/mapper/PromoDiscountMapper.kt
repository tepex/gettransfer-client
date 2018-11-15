package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.PromoDiscountEntity
import com.kg.gettransfer.domain.model.PromoDiscount

class PromoDiscountMapper: Mapper<PromoDiscountEntity, PromoDiscount> {
    override fun toEntity(type: PromoDiscount) = PromoDiscountEntity(type.discount)
    override fun fromEntity(type: PromoDiscountEntity) = PromoDiscount(type.discountText)
}
