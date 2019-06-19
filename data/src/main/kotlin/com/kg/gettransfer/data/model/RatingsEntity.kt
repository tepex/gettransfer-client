package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.Ratings

import kotlinx.serialization.Optional
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class RatingsEntity(
        @Optional@SerialName(AVERAGE) val average: Float? = null,
        @Optional@SerialName(VEHICLE) val vehicle: Float? = null,
        @Optional@SerialName(DRIVER) val driver:   Float? = null,
        @Optional@SerialName(FAIR) val fair:       Float? = null
) {

    companion object {
        const val AVERAGE = "average"
        const val   VEHICLE = "vehicle"
        const val DRIVER  = "driver"
        const val FAIR    = "fair"
    }
}

fun Ratings.map() = RatingsEntity(average, vehicle, driver, fair)
fun RatingsEntity.map() = Ratings(average, vehicle, driver, fair)
