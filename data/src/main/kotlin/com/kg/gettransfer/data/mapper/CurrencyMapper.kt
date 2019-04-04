package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.CurrencyEntity
import com.kg.gettransfer.domain.model.Currency

/**
 * Map a [CurrencyEntity] to and from a [Currency] instance when data is moving between this later and the Domain layer.
 */
open class CurrencyMapper : Mapper<CurrencyEntity, Currency> {
    override fun fromEntity(type: CurrencyEntity) =
            Currency(
                    code = type.code,
                    symbol = type.symbol
            )
    override fun toEntity(type: Currency): CurrencyEntity { throw UnsupportedOperationException() }
}
