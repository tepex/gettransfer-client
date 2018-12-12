package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kg.gettransfer.data.model.VehicleEntity

class VehicleModel(
    name: String,
    regNumber: String,
    @SerializedName(VehicleEntity.ID) @Expose val id: Long,
    @SerializedName(VehicleEntity.YEAR) @Expose val year: Int,
    @SerializedName(VehicleEntity.COLOR) @Expose val color: String?,
    @SerializedName(VehicleEntity.TRANSPORT_TYPE_ID) @Expose val transportTypeId: String,
    @SerializedName(VehicleEntity.PAX_MAX) @Expose val paxMax: Int,
    @SerializedName(VehicleEntity.LUGGAGE_MAX) @Expose val luggageMax: Int,
    @SerializedName(VehicleEntity.PHOTOS) @Expose var photos: List<String>
): VehicleBaseModel(name, regNumber)
