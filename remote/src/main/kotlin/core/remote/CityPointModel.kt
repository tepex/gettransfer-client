package com.kg.gettransfer.core.remote

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kg.gettransfer.core.data.CityPointEntity

data class CityPointModel(
    @SerializedName(CityPointEntity.NAME) @Expose val name: String,
    @SerializedName(CityPointEntity.POINT) @Expose val point: String?,
    @SerializedName(CityPointEntity.PLACE_ID) @Expose val placeId: String?
)

fun CityPointModel.map() = CityPointEntity(name, point, placeId)

fun CityPointEntity.map() = CityPointModel(name ?: "", point, placeId)
