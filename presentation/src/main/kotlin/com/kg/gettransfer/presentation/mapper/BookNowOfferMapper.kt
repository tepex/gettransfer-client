package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.BookNowOffer

import com.kg.gettransfer.presentation.model.BookNowOfferModel
import com.kg.gettransfer.presentation.model.map

import org.koin.core.get

open class BookNowOfferMapper : Mapper<BookNowOfferModel, BookNowOffer> {
    private val moneyMapper  = get<MoneyMapper>()

    override fun toView(type: BookNowOffer) =
        BookNowOfferModel(
            amount          = type.amount,
            base            = moneyMapper.toView(type.base),
            withoutDiscount = type.withoutDiscount?.let { moneyMapper.toView(it) },
            transportType   = type.transportType.map()
        )

    override fun fromView(type: BookNowOfferModel): BookNowOffer { throw UnsupportedOperationException() }
}
