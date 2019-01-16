package com.kg.gettransfer.cache.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import com.kg.gettransfer.data.model.BookNowOfferEntity
import kotlinx.serialization.Serializable

@Serializable
data class BookNowOfferCached (
        @ColumnInfo(name = BookNowOfferEntity.AMOUNT) val amount: Double,
        @Embedded(prefix = BookNowOfferEntity.BASE) val base: MoneyCached,
        @Embedded(prefix = BookNowOfferEntity.WITHOUT_DISCOUNT) val withoutDiscount: MoneyCached?
)

@Serializable
data class BookNowOfferCachedMap (val map: Map<String, BookNowOfferCached>)