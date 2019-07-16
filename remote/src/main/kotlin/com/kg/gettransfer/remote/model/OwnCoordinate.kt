package com.kg.gettransfer.remote.model

import com.kg.gettransfer.data.model.CoordinateEntity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OwnCoordinate(
    @SerialName(CoordinateEntity.LATITUDE) val lat: String,
    @SerialName(CoordinateEntity.LONGITUDE) val lon: String
)
