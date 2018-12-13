package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kg.gettransfer.data.model.VehicleBaseEntity

open class VehicleBaseModel(
    @SerializedName(VehicleBaseEntity.NAME) @Expose val name: String,
    @SerializedName(VehicleBaseEntity.REGISTRATION_NUMBER) @Expose val registrationNumber: String
)
