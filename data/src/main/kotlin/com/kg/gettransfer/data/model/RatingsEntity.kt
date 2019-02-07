package com.kg.gettransfer.data.model

import kotlinx.serialization.Optional
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class RatingsEntity(
        @SerialName(AVERAGE)           val average: Float?,
        @Optional @SerialName(VEHICLE) val vehicle: Float? = null,
        @Optional @SerialName(DRIVER)  val driver: Float? = null,
        @Optional @SerialName(FAIR)    val fair: Float? = null
) {

    companion object {
        const val AVERAGE = "average"
        const val VEHICLE = "vehicle"
        const val DRIVER  = "driver"
        const val FAIR    = "fair"
    }
}
