package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.MoneyEntity

import com.kg.gettransfer.domain.model.Money

/**
 * Map a [MoneyEntity] to and from a [Money] instance when data is moving between
 * this later and the Domain layer.
 */
open class MoneyMapper : Mapper<MoneyEntity, Money> {
    /**
     * Map a [MoneyEntity] instance to a [Money] instance.
     */
    override fun fromEntity(type: MoneyEntity) =
        Money(
            default = type.def,
            preferred = type.preferred
        )
    /**
     * Map a [Money] instance to a [MoneyEntity] instance.
     */
    override fun toEntity(type: Money) =
        MoneyEntity(
            def = type.default,
            preferred = type.preferred
        )
}
