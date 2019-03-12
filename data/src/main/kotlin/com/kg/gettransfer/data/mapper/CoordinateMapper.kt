package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.CoordinateEntity
import com.kg.gettransfer.domain.model.Coordinate

class CoordinateMapper: Mapper<CoordinateEntity, Coordinate> {
    override fun fromEntity(type: CoordinateEntity) = Coordinate(transferId = type.transferId, lat = type.lat.toDouble(), lon = type.lon.toDouble())
    override fun toEntity(type: Coordinate) = CoordinateEntity(lat = type.lat.toFloat(), lon = type.lon.toFloat())
}