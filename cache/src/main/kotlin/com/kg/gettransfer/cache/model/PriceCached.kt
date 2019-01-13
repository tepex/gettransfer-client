package com.kg.gettransfer.cache.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import com.kg.gettransfer.data.model.PriceEntity

data class PriceCached(
        @Embedded(prefix = PriceEntity.BASE) val base: MoneyCached,
        @Embedded(prefix = PriceEntity.NO_DISCOUNT) val withoutDiscount: MoneyCached?,
        @ColumnInfo(name = PriceEntity.PERCENTAGE_30) val percentage30: String,
        @ColumnInfo(name = PriceEntity.PERCENTAGE_70) val percentage70: String,
        @ColumnInfo(name = PriceEntity.AMOUNT) val amount: Double
)