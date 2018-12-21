package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.CurrencyEntity

import java.util.Currency
/**
 * Map a [CurrencyEntity] to and from a [Currency] instance when data is moving between this later and the Domain layer.
 */
open class CurrencyMapper : Mapper<CurrencyEntity, Currency> {
    override fun fromEntity(type: CurrencyEntity) = Currency.getInstance(type.code)!!
    override fun toEntity(type: Currency): CurrencyEntity { throw UnsupportedOperationException() }
}
