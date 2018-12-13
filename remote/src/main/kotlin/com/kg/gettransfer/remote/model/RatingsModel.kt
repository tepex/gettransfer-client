package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kg.gettransfer.data.model.RatingsEntity

class RatingsModel(
    @SerializedName(RatingsEntity.AVERAGE) @Expose val average: Float?,
    @SerializedName(RatingsEntity.VEHICLE) @Expose val vehicle: Float?,
    @SerializedName(RatingsEntity.DRIVER) @Expose val driver: Float?,
    @SerializedName(RatingsEntity.FAIR) @Expose val fair: Float?
)
