package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kg.gettransfer.data.model.RatingsEntity

data class RatingsModel(
    @SerializedName(RatingsEntity.AVERAGE) @Expose val average: Double?,
    @SerializedName(RatingsEntity.VEHICLE) @Expose val vehicle: Double?,
    @SerializedName(RatingsEntity.DRIVER) @Expose val driver: Double?,
    @SerializedName(RatingsEntity.FAIR) @Expose val fair: Double?
)

fun RatingsModel.map() = RatingsEntity(average, vehicle, driver, fair)
