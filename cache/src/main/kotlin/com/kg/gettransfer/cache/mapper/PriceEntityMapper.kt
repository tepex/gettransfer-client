package com.kg.gettransfer.cache.mapper

import com.kg.gettransfer.cache.model.PriceCached
import com.kg.gettransfer.data.model.PriceEntity

import org.koin.standalone.get

open class PriceEntityMapper : EntityMapper<PriceCached, PriceEntity> {
    private val moneyMapper = get<MoneyEntityMapper>()

    override fun fromCached(type: PriceCached) =
            PriceEntity(
                    base = moneyMapper.fromCached(type.base),
                    withoutDiscount = type.withoutDiscount?.let { moneyMapper.fromCached(it)},
                    percentage30 = type.percentage30,
                    percentage70 = type.percentage70,
                    amount = type.amount
            )

    override fun toCached(type: PriceEntity) =
            PriceCached(
                    base = moneyMapper.toCached(type.base),
                    withoutDiscount = type.withoutDiscount?.let { moneyMapper.toCached(it)},
                    percentage30 = type.percentage30,
                    percentage70 = type.percentage70,
                    amount = type.amount
            )
}