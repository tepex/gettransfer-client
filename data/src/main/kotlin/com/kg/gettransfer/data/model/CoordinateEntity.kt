package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.Coordinate

import kotlinx.serialization.Optional
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoordinateEntity(
    @Optional var transferId: Long? = null,
    @SerialName(LATITUDE) val lat: String,
    @SerialName(LONGITUDE) val lon: String
) {

    companion object {
        const val LATITUDE  = "lat"
        const val LONGITUDE = "lng"
    }
}

fun Coordinate.map() = CoordinateEntity(null, lat.toString(), lon.toString())
fun CoordinateEntity.map() = Coordinate(transferId, lat.toDouble(), lon.toDouble())
