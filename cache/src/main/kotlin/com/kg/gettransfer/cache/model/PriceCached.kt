package com.kg.gettransfer.cache.model

import androidx.room.ColumnInfo
import androidx.room.Embedded

import com.kg.gettransfer.data.model.PriceEntity

data class PriceCached(
    @Embedded(prefix = PriceEntity.BASE) val base: MoneyCached,
    @Embedded(prefix = PriceEntity.NO_DISCOUNT) val withoutDiscount: MoneyCached?,
    @ColumnInfo(name = PriceEntity.PERCENTAGE_30) val percentage30: String,
    @ColumnInfo(name = PriceEntity.PERCENTAGE_70) val percentage70: String,
    @ColumnInfo(name = PriceEntity.AMOUNT) val amount: Double
)

fun PriceCached.map() =
    PriceEntity(
        base.map(),
        withoutDiscount?.map(),
        percentage30,
        percentage70,
        amount
    )

fun PriceEntity.map() =
    PriceCached(
        base.map(),
        withoutDiscount?.map(),
        percentage30,
        percentage70,
        amount
    )
