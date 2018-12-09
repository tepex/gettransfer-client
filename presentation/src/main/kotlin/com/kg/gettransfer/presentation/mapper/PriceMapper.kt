package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.Price

import com.kg.gettransfer.presentation.model.PriceModel

import org.koin.standalone.get

open class PriceMapper : Mapper<PriceModel, Price> {
    private val moneyMapper = get<MoneyMapper>()

    override fun toView(type: Price) =
        PriceModel(
            moneyMapper.toView(type.base),
            type.withoutDiscount?.let { moneyMapper.toView(it) },
            type.percentage30,
            type.percentage70,
            "%.2f".format(type.amount)
        )

    override fun fromView(type: PriceModel): Price { throw UnsupportedOperationException() }
}
