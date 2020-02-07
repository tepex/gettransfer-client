package com.kg.gettransfer.core.cache

import androidx.room.ColumnInfo

import com.kg.gettransfer.core.data.CityPointEntity

data class CityPointModel(
    @ColumnInfo(name = CityPointEntity.NAME) var name: String?,
    @ColumnInfo(name = CityPointEntity.POINT) var point: String?,
    @ColumnInfo(name = CityPointEntity.PLACE_ID) var placeId: String?
)

fun CityPointModel.map() = CityPointEntity(name ?: "", point, placeId)

fun CityPointEntity.map() = CityPointModel(name, point, placeId)
