package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.LocationEntity
import com.kg.gettransfer.domain.model.Location

class LocationMapper: Mapper<LocationEntity, Location> {
    override fun fromEntity(type: LocationEntity): Location =
            Location(type.latitude, type.longitude)

    override fun toEntity(type: Location): LocationEntity {
        throw UnsupportedOperationException()
    }
}