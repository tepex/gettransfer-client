package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.model.TransportTypeEntity
import com.kg.gettransfer.data.model.VehicleEntity

data class VehicleModel(
    @SerializedName(VehicleEntity.ID) @Expose val id: Long,
    @SerializedName(VehicleEntity.NAME) @Expose val name: String,
    @SerializedName(VehicleEntity.MODEL) @Expose val model: String,
    @SerializedName(VehicleEntity.REGISTRATION_NUMBER) @Expose val registrationNumber: String?,
    @SerializedName(VehicleEntity.YEAR) @Expose val year: Int,
    @SerializedName(VehicleEntity.COLOR) @Expose val color: String?,

    @SerializedName(TransportTypeEntity.TRANSPORT_TYPE_ID) @Expose val transportTypeId: String,
    @SerializedName(TransportTypeEntity.PAX_MAX) @Expose val paxMax: Int,
    @SerializedName(TransportTypeEntity.LUGGAGE_MAX) @Expose val luggageMax: Int,

    @SerializedName(VehicleEntity.PHOTOS) @Expose val photos: List<String>
)

fun VehicleModel.map() =
    VehicleEntity(
        id,
        name,
        model,
        registrationNumber,
        year,
        color,
        transportTypeId,
        paxMax,
        luggageMax,
        photos
    )
