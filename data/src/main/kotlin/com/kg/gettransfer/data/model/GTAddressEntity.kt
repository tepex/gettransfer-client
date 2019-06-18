package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.CityPoint
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point

import kotlinx.serialization.Optional
import kotlinx.serialization.Serializable

@Serializable
data class GTAddressEntity(
    val lat: Double?,
    val lon: Double?,
    val address: String,
    val placeTypes: List<String>?,
    @Optional
    val variants: Pair<String?, String?>? = null
)

fun GTAddress.map() =
    GTAddressEntity(
        cityPoint.point.latitude,
        cityPoint.point.longitude,
        address,
        placeTypes,
        variants
    )
fun GTAddressEntity.map() =
    GTAddress(
        CityPoint(
            address,
            lat?.let { lat -> lon?.let { Point(lat, it) } } ?: Point.EMPTY,
            ""
        ),
        placeTypes,
        address,
        variants
    )
