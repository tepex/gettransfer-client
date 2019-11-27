package com.kg.gettransfer.cache.model

import androidx.room.ColumnInfo
import androidx.room.Embedded

import com.kg.gettransfer.data.model.PriceEntity

data class PriceCached(
    @Embedded(prefix = PriceEntity.BASE) val base: MoneyCached,
    @Embedded(prefix = PriceEntity.NO_DISCOUNT) val withoutDiscount: MoneyCached?,
    @ColumnInfo(name = PriceEntity.AMOUNT) val amount: Double
)

fun PriceCached.map() =
    PriceEntity(
        base.map(),
        withoutDiscount?.map(),
        amount
    )

fun PriceEntity.map() =
    PriceCached(
        base.map(),
        withoutDiscount?.map(),
        amount
    )
