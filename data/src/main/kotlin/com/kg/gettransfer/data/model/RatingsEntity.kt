package com.kg.gettransfer.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class RatingsEntity(
    @SerialName(AVERAGE) val average: Float?,
    @SerialName(VEHICLE) val vehicle: Float?,
    @SerialName(DRIVER) val driver:   Float?,
    @SerialName(FAIR) val fair:       Float?
) {

    companion object {
        const val AVERAGE = "average"
        const val VEHICLE = "vehicle"
        const val DRIVER  = "driver"
        const val FAIR    = "fair"
    }
}
