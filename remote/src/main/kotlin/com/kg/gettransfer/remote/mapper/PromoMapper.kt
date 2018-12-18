package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.PromoDiscountEntity

open class PromoMapper : EntityMapper<String, PromoDiscountEntity> {
    override fun fromRemote(type: String) = PromoDiscountEntity(type)
    override fun toRemote(type: PromoDiscountEntity): String { throw UnsupportedOperationException() }
}
