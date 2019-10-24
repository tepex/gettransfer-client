package com.kg.gettransfer.cache.model

import androidx.room.ColumnInfo

import com.kg.gettransfer.data.model.CityPointEntity

data class CityPointCached(
    @ColumnInfo(name = CityPointEntity.NAME) var name: String?,
    @ColumnInfo(name = CityPointEntity.POINT) var point: String?,
    @ColumnInfo(name = CityPointEntity.PLACE_ID) var placeId: String?
)

fun CityPointCached.map() = CityPointEntity(name ?: "", point, placeId)

fun CityPointEntity.map() = CityPointCached(name, point, placeId)
