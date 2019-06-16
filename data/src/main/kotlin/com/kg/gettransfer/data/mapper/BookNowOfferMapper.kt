package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.BookNowOfferEntity

import com.kg.gettransfer.domain.model.BookNowOffer
import com.kg.gettransfer.domain.model.TransportType

import org.koin.standalone.KoinComponent
import org.koin.standalone.get

open class BookNowOfferMapper : KoinComponent {

    private val moneyMapper = get<MoneyMapper>()

    fun fromEntity(type: BookNowOfferEntity, transportType: TransportType) =
        BookNowOffer(
            amount          = type.amount,
            base            = moneyMapper.fromEntity(type.base),
            withoutDiscount = type.withoutDiscount?.let { moneyMapper.fromEntity(it) },
            transportType   = transportType
        )
}
