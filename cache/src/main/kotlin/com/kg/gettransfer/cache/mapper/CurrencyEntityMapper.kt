package com.kg.gettransfer.cache.mapper

import com.kg.gettransfer.cache.model.CurrencyCached

import com.kg.gettransfer.data.model.CurrencyEntity

class CurrencyEntityMapper : EntityMapper<CurrencyCached, CurrencyEntity> {
    override fun fromCached(type: CurrencyCached) = CurrencyEntity(type.code, type.symbol)
    override fun toCached(type: CurrencyEntity) = CurrencyCached(type.code, type.symbol)
}
