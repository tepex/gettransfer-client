package com.kg.gettransfer.cache.mapper

import com.kg.gettransfer.cache.model.StringList
import com.kg.gettransfer.cache.model.VehicleCached
import com.kg.gettransfer.data.model.VehicleEntity

open class VehicleEntityMapper : EntityMapper<VehicleCached, VehicleEntity> {

    override fun fromCached(type: VehicleCached) =
            VehicleEntity(
                    id = type.id,
                    name = type.name,
                    registrationNumber = type.registrationNumber,
                    year = type.year,
                    color = type.color,
                    transportTypeId = type.transportTypeId,
                    paxMax = type.paxMax,
                    luggageMax = type.luggageMax,
                    photos = type.photos.list
            )

    override fun toCached(type: VehicleEntity) =
            VehicleCached(
                    id = type.id,
                    name = type.name,
                    registrationNumber = type.registrationNumber,
                    year = type.year,
                    color = type.color,
                    transportTypeId = type.transportTypeId,
                    paxMax = type.paxMax,
                    luggageMax = type.luggageMax,
                    photos = StringList(type.photos)
            )
}