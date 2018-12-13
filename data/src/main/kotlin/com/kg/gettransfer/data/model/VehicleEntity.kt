package com.kg.gettransfer.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class VehicleEntity(
    @SerialName(ID) val id: Long,
    @SerialName(VEHICLE_BASE) val vehicleBase: VehicleBaseEntity,
    @SerialName(YEAR) val year: Int,
    @SerialName(COLOR) val color: String?,
    @SerialName(TRANSPORT_TYPE) val transportType: TransportTypeEntity,
    @SerialName(PHOTOS) val photos: List<String>
) {

    companion object {
        const val ID             = "id"
        const val VEHICLE_BASE   = "vehicle_base"
        const val YEAR           = "year"
        const val COLOR          = "color"
        const val TRANSPORT_TYPE = "transport_type"
        const val PHOTOS         = "photos"
    }
}
