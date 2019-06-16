package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.GTAddressEntity

import com.kg.gettransfer.domain.model.CityPoint
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point

class AddressMapper : Mapper<GTAddressEntity, GTAddress> {
    override fun fromEntity(type: GTAddressEntity): GTAddress =
        GTAddress(
            cityPoint = CityPoint(
                name = type.address,
                point = type.lat?.let { lat -> type.lon?.let { lon -> Point(lat, lon) } } ?: Point(0.0, 0.0),
                placeId = null),
            placeTypes = type.placeTypes,
            address = type.address,
            variants = type.variants
        )

    override fun toEntity(type: GTAddress) =
        GTAddressEntity(
            lat = type.cityPoint.point?.latitude,
            lon = type.cityPoint.point?.longitude,
            address = type.address ?: "",
            placeTypes = type.placeTypes,
            variants = type.variants
        )
}
