package com.kg.gettransfer.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class TransportTypeEntity(
    @SerialName(ID) val id: String,
    @SerialName(PAX_MAX) val paxMax: Int,
    @SerialName(LUGGAGE_MAX) val luggageMax: Int
) {

    companion object {
        const val ID                = "id"
        const val TRANSPORT_TYPE_ID = "transport_type_id"
        const val PAX_MAX           = "pax_max"
        const val LUGGAGE_MAX       = "luggage_max"
    }
}
