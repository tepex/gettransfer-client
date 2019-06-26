package com.kg.gettransfer.cache.model

import android.arch.persistence.room.ColumnInfo
import com.kg.gettransfer.data.model.RatingsEntity

data class RatingsCached(
    @ColumnInfo(name = RatingsEntity.AVERAGE) val average: Double?,
    @ColumnInfo(name = RatingsEntity.VEHICLE) val vehicle: Double?,
    @ColumnInfo(name = RatingsEntity.DRIVER) val driver: Double?,
    @ColumnInfo(name = RatingsEntity.FAIR) val fair: Double?
)

fun RatingsCached.map() = RatingsEntity(average, vehicle, driver, fair)
fun RatingsEntity.map() = RatingsCached(average, vehicle, driver, fair)
