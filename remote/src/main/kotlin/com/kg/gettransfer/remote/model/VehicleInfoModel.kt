package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kg.gettransfer.data.model.VehicleInfoEntity

data class VehicleInfoModel(
    @SerializedName(VehicleInfoEntity.NAME) @Expose val name: String,
    @SerializedName(VehicleInfoEntity.REGISTRATION_NUMBER) @Expose val registrationNumber: String
)

fun VehicleInfoModel.map() = VehicleInfoEntity(name, registrationNumber)

fun VehicleInfoEntity.map() = VehicleInfoModel(name, registrationNumber)
