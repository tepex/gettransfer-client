package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.LocationEntity
import com.kg.gettransfer.domain.model.Point

class LocationMapper: Mapper<LocationEntity, Point> {
    override fun fromEntity(type: LocationEntity) =
            Point(type.latitude, type.longitude)

    override fun toEntity(type: Point) =
            LocationEntity(type.latitude, type.longitude)
}