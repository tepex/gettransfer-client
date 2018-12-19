package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.BookNowOfferEntity
import com.kg.gettransfer.remote.model.BookNowOfferModel

import org.koin.standalone.get

open class BookNowOfferMapper : EntityMapper<Map.Entry<String, BookNowOfferModel>, BookNowOfferEntity> {

    private var moneyMapper = get<MoneyMapper>()

    override fun fromRemote(type: Map.Entry<String, BookNowOfferModel>): BookNowOfferEntity =
        BookNowOfferEntity(
            amount = type.value.amount,
            base = moneyMapper.fromRemote(type.value.base),
            withoutDiscount = type.value.withoutDiscount?.let { moneyMapper.fromRemote(it) }
        )

    override fun toRemote(type: BookNowOfferEntity): Map.Entry<String, BookNowOfferModel> {
        throw UnsupportedOperationException()
    }
}
