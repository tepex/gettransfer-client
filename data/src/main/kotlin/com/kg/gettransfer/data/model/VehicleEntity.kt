package com.kg.gettransfer.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class VehicleEntity(
    @SerialName(ID) val id: Long,
//    @SerialName(VEHICLE_BASE) val vehicleBase: VehicleBaseEntity,
    @SerialName(NAME) val name: String,
    @SerialName(REGISTRATION_NUMBER) val registrationNumber: String,
    @SerialName(YEAR) val year: Int,
    @SerialName(COLOR) val color: String?,
//    @SerialName(TRANSPORT_TYPE) val transportType: TransportTypeEntity,
    @SerialName(TRANSPORT_TYPE_ID) val transportTypeId: String,
    @SerialName(PAX_MAX) val paxMax: Int,
    @SerialName(LUGGAGE_MAX) val luggageMax: Int,
    @SerialName(PHOTOS) val photos: List<String>
) {

    companion object {
        const val ID             = "id"
//        const val VEHICLE_BASE   = "vehicle_base"
        const val NAME                = "name"
        const val REGISTRATION_NUMBER = "registration_number"
        const val YEAR           = "year"
        const val COLOR          = "color"
//        const val TRANSPORT_TYPE = "transport_type"
        const val TRANSPORT_TYPE_ID = "transport_type_id"
        const val PAX_MAX           = "pax_max"
        const val LUGGAGE_MAX       = "luggage_max"
        const val PHOTOS         = "photos"
    }
}
