package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.Ratings

import kotlinx.serialization.Optional
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class RatingsEntity(
    @Optional@SerialName(AVERAGE) val average:             Double? = null,
    @Optional@SerialName(VEHICLE) val vehicle:             Double? = null,
    @Optional@SerialName(DRIVER) val driver:               Double? = null,
    @Optional@SerialName(COMMUNICATION) val communication: Double? = null
) {

    companion object {
        const val AVERAGE       = "average"
        const val VEHICLE       = "vehicle"
        const val DRIVER        = "driver"
        const val COMMUNICATION = "communication"
    }
}

fun Ratings.map() = RatingsEntity(average, vehicle, driver, communication)
fun RatingsEntity.map() = Ratings(average, vehicle, driver, communication)
