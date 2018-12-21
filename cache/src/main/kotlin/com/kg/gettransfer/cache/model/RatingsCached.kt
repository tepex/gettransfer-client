package com.kg.gettransfer.cache.model

import android.arch.persistence.room.ColumnInfo

import com.kg.gettransfer.data.model.RatingsEntity

data class RatingsCached(
    @ColumnInfo(name = RatingsEntity.AVERAGE) val average: Float?,
    @ColumnInfo(name = RatingsEntity.VEHICLE) val vehicle: Float?,
    @ColumnInfo(name = RatingsEntity.DRIVER) val driver: Float?,
    @ColumnInfo(name = RatingsEntity.FAIR) val fair: Float?
)
