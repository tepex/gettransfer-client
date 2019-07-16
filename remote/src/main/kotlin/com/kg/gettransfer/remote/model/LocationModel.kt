package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.model.LocationEntity

class LocationModel(
    @SerializedName(LocationEntity.LATITUDE) @Expose val latitude: Double = 0.0,
    @SerializedName(LocationEntity.LONGITUDE) @Expose val longitude: Double = 0.0
)

fun LocationModel.map() = LocationEntity(latitude, longitude)
