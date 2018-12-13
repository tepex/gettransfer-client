package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kg.gettransfer.data.model.TransportTypeEntity
import com.kg.gettransfer.data.model.VehicleEntity

class VehicleModel(
    name: String,
    regNumber: String,
    @SerializedName(VehicleEntity.ID) @Expose val id: Long,
    @SerializedName(VehicleEntity.YEAR) @Expose val year: Int,
    @SerializedName(VehicleEntity.COLOR) @Expose val color: String?,
    @SerializedName(TransportTypeEntity.TRANSPORT_TYPE_ID) @Expose val transportTypeId: String,
    @SerializedName(TransportTypeEntity.PAX_MAX) @Expose val paxMax: Int,
    @SerializedName(TransportTypeEntity.LUGGAGE_MAX) @Expose val luggageMax: Int,
    @SerializedName(VehicleEntity.PHOTOS) @Expose var photos: List<String>
): VehicleBaseModel(name, regNumber)
