package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.PriceEntity

import com.kg.gettransfer.domain.model.Price

import org.koin.standalone.get

/**
 * Map a [PriceEntity] to and from a [Price] instance when data is moving between
 * this later and the Domain layer.
 */
open class PriceMapper : Mapper<PriceEntity, Price> {
    private val moneyMapper = get<MoneyMapper>()

    /**
     * Map a [PriceEntity] instance to a [Price] instance.
     */
    override fun fromEntity(type: PriceEntity) =
        Price(
            base = moneyMapper.fromEntity(type.base),
            withoutDiscount = type.withoutDiscount?.let { moneyMapper.fromEntity(it) },
            percentage30 = type.percentage30,
            percentage70 = type.percentage70,
            amount = type.amount
        )
    /**
     * Map a [Price] instance to a [PriceEntity] instance.
     */
    override fun toEntity(type: Price) =
        PriceEntity(
            base = moneyMapper.toEntity(type.base),
            withoutDiscount = type.withoutDiscount?.let { moneyMapper.toEntity(it) },
            percentage30 = type.percentage30,
            percentage70 = type.percentage70,
            amount = type.amount
        )
}
