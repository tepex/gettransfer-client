package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.GTAddressEntity
import com.kg.gettransfer.domain.model.CityPoint
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point

class AddressMapper(): Mapper<GTAddressEntity, GTAddress> {
    override fun fromEntity(type: GTAddressEntity): GTAddress =
            GTAddress(CityPoint(type.address, Point(type.lat, type.lon), null ), null, null, null, null)

    override fun toEntity(type: GTAddress): GTAddressEntity =
            GTAddressEntity(type.cityPoint.point!!.latitude, type.cityPoint.point!!.longitude, type.address!!)

}