package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.Money

import com.kg.gettransfer.presentation.model.MoneyModel

open class MoneyMapper : Mapper<MoneyModel, Money> {
    override fun toView(type: Money) =
        MoneyModel(
            def       = type.def,
            preferred = type.preferred
        )

    override fun fromView(type: MoneyModel): Money { throw UnsupportedOperationException() }
}
