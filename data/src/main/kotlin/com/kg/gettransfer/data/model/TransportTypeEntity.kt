package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.TransportType

import java.util.Locale

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

fun TransportType.map() = TransportTypeEntity(id.toString(), paxMax, luggageMax)

fun TransportTypeEntity.map() = TransportType(id.map(), paxMax, luggageMax)

fun String.map(): TransportType.ID =
    try {
        enumValueOf<TransportType.ID>(toUpperCase(Locale.US))
    } catch (e: IllegalArgumentException) { TransportType.ID.UNKNOWN }

fun TransportType.ID.map() = toString()
