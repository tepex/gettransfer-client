package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.PromoEntity
import com.kg.gettransfer.domain.model.PromoDiscount

class PromoDiscountMapper: Mapper<PromoEntity?, PromoDiscount> {

    override fun toEntity(type: PromoDiscount): PromoEntity? = null
    override fun fromEntity(type: PromoEntity?): PromoDiscount = PromoDiscount(type!!.discountText)
}