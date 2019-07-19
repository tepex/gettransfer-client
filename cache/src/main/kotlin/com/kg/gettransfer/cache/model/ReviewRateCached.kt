package com.kg.gettransfer.cache.model

import android.arch.persistence.room.ColumnInfo

import com.kg.gettransfer.data.model.ReviewRateEntity

data class ReviewRateCached(
        @ColumnInfo(name = ReviewRateEntity.RATE_TYPE) val rateType: String,
        @ColumnInfo(name = ReviewRateEntity.TOTAL_RATING) val value: Int
)

fun ReviewRateCached.map() = ReviewRateEntity(rateType, value)
fun ReviewRateEntity.map() = ReviewRateCached(rateType, value)