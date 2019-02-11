package com.kg.gettransfer.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OwnCoordinate(@SerialName(LATITUDE) val lat: Float,
                         @SerialName(LONGITUDE) val lon: Float)
{
    companion object {
        const val LATITUDE  = "lat"
        const val LONGITUDE = "lng"
    }
}