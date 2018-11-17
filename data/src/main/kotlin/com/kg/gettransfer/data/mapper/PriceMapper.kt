package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.PriceEntity

import com.kg.gettransfer.domain.model.Price

import org.koin.standalone.get

/**
 * Map a [PriceEntity] to and from a [Price] instance when data is moving between
 * this later and the Domain layer.
 */
open class PriceMapper: Mapper<PriceEntity, Price> {
    private val moneyMapper = get<MoneyMapper>()

    /**
     * Map a [PriceEntity] instance to a [Price] instance.
     */
    override fun fromEntity(type: PriceEntity) = 
        Price(moneyMapper.fromEntity(type.base), type.withoutDiscount?.let {moneyMapper.fromEntity(it)}, type.percentage30, type.percentage70, type.amount)
    /**
     * Map a [Price] instance to a [PriceEntity] instance.
     */
    override fun toEntity(type: Price) = 
        PriceEntity(moneyMapper.toEntity(type.base), type.withoutDiscount?.let {moneyMapper.toEntity(it)}, type.percentage30, type.percentage70, type.amount)
}
