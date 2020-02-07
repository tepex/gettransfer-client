package com.kg.gettransfer.data.model

import com.kg.gettransfer.core.domain.Point

data class LocationEntity(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) {

    companion object {
        const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"
    }
}

fun LocationEntity.map() = Point(latitude, longitude)

fun Point.map() = LocationEntity(latitude, longitude)
