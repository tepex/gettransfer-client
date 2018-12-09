package com.kg.gettransfer.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class VehicleEntity(
    @SerialName(NAME) val name: String,
    @SerialName(REGISTRATION_NUMBER) val registrationNumber: String,
    @SerialName(YEAR) val year: Int,
    @SerialName(COLOR) val color: String?,
    @SerialName(TRANSPORT_TYPE_ID) val transportTypeId: String,
    @SerialName(PAX_MAX) val paxMax: Int,
    @SerialName(LUGGAGE_MAX) val luggageMax: Int,
    @SerialName(PHOTOS) val photos: List<String>
) {

    companion object {
        const val NAME                = "name"
        const val REGISTRATION_NUMBER = "registration_number"
        const val YEAR                = "year"
        const val COLOR               = "color"
        const val TRANSPORT_TYPE_ID   = "transport_type_id"
        const val PAX_MAX             = "pax_max"
        const val LUGGAGE_MAX         = "luggage_max"
        const val PHOTOS              = "photos"
    }
}
