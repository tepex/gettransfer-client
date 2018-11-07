package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kg.gettransfer.data.model.VehicleEntity

open class VehicleBaseModel(@SerializedName(VehicleEntity.NAME) @Expose val name: String,
                            @SerializedName(VehicleEntity.REGISTRATION_NUMBER) @Expose val registrationNumber: String)
