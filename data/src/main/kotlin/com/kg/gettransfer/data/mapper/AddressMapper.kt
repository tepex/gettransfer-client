package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.GTAddressEntity

import com.kg.gettransfer.domain.model.CityPoint
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point

class AddressMapper(): Mapper<GTAddressEntity, GTAddress> {
    override fun fromEntity(type: GTAddressEntity): GTAddress {
        var point: Point? = null
        if(type.lat != null && type.lon != null) point = Point(type.lat, type.lon)
        return GTAddress(CityPoint(type.address, point, null),
                         type.placeTypes,
                         type.address,
                         type.primary,
                         type.secondary)
    }

    override fun toEntity(type: GTAddress) =
        GTAddressEntity(type.cityPoint.point?.latitude,
                        type.cityPoint.point?.longitude,
                        type.address!!,
                        type.placeTypes,
                        type.primary,
                        type.secondary)
}