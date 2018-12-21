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
                point = type.lat?.let { lat -> type.lon?.let { lon -> Point(lat, lon) } },
                placeId = null),
            placeTypes = type.placeTypes,
            address = type.address,
            primary = type.primary,
            secondary = type.secondary
        )

    override fun toEntity(type: GTAddress) =
        GTAddressEntity(
            lat = type.cityPoint.point?.latitude,
            lon = type.cityPoint.point?.longitude,
            address = type.address!!,
            placeTypes = type.placeTypes,
            primary = type.primary,
            secondary = type.secondary
        )
}
