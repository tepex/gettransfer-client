package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.BookNowOfferEntity
import com.kg.gettransfer.domain.model.BookNowOffer

import org.koin.standalone.get

open class BookNowOfferMapper : Mapper<BookNowOfferEntity, BookNowOffer> {

    private val moneyMapper = get<MoneyMapper>()

    override fun fromEntity(type: BookNowOfferEntity): BookNowOffer =
        BookNowOffer(
            transportTypeId = type.transportTypeId,
            amount = type.amount,
            base = moneyMapper.fromEntity(type.base),
            withoutDiscount = type.withoutDiscount?.let { moneyMapper.fromEntity(it) }
        )

    override fun toEntity(type: BookNowOffer): BookNowOfferEntity =
        BookNowOfferEntity(
            transportTypeId = type.transportTypeId,
            amount = type.amount,
            base = moneyMapper.toEntity(type.base),
            withoutDiscount = type.withoutDiscount?.let { moneyMapper.toEntity(it) }
        )
}
