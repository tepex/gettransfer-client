package com.kg.gettransfer.cache.mapper

import com.kg.gettransfer.cache.model.BookNowOfferCached
import com.kg.gettransfer.data.model.BookNowOfferEntity

import org.koin.standalone.get

open class BookNowOfferEntityMapper : EntityMapper<BookNowOfferCached, BookNowOfferEntity> {
    private val moneyMapper = get<MoneyEntityMapper>()

    override fun fromCached(type: BookNowOfferCached) =
            BookNowOfferEntity(
                    amount = type.amount,
                    base = moneyMapper.fromCached(type.base),
                    withoutDiscount = type.withoutDiscount?.let { moneyMapper.fromCached(it) }
            )

    override fun toCached(type: BookNowOfferEntity) =
            BookNowOfferCached(
                    amount = type.amount,
                    base = moneyMapper.toCached(type.base),
                    withoutDiscount = type.withoutDiscount?.let { moneyMapper.toCached(it) }
            )
}