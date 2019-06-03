package com.kg.gettransfer.data.model

import com.kg.gettransfer.data.mapper.Mapper
import com.kg.gettransfer.domain.model.Point

open class PlaceLocationMapper : Mapper<PlaceLocationEntity, Point> {
    override fun fromEntity(type: PlaceLocationEntity) =
            Point(
                latitude  = type.lat,
                longitude = type.lng
            )

    override fun toEntity(type: Point): PlaceLocationEntity {
        throw UnsupportedOperationException()
    }
}