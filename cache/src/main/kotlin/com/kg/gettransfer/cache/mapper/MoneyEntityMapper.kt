package com.kg.gettransfer.cache.mapper

import com.kg.gettransfer.cache.model.MoneyCached

import com.kg.gettransfer.data.model.MoneyEntity

class MoneyEntityMapper: EntityMapper<MoneyCached, MoneyEntity> {
    override fun fromCached(type: MoneyCached) = MoneyEntity(type.default, type.preferred)
    override fun toCached(type: MoneyEntity) = MoneyCached(type.default, type.preferred)
}
