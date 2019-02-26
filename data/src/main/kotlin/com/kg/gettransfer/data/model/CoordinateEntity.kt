package com.kg.gettransfer.data.model

import kotlinx.serialization.Optional
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoordinateEntity(@Optional var transferId: Long? = 0,
                            @SerialName(LATITUDE) val lat: Float,
                            @SerialName(LONGITUDE) val lon: Float) {
    companion object {
        const val LATITUDE  = "lat"
        const val LONGITUDE = "lng"
    }
}