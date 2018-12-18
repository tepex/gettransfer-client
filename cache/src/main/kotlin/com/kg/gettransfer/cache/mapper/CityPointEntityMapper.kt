package com.kg.gettransfer.cache.mapper

import com.kg.gettransfer.cache.model.CityPointCached

import com.kg.gettransfer.data.model.CityPointEntity

open class CityPointEntityMapper : EntityMapper<CityPointCached, CityPointEntity> {
    override fun fromCached(type: CityPointCached) =
        CityPointEntity(
            name = type.name,
            point = type.point,
            placeId = type.placeId
        )

    override fun toCached(type: CityPointEntity) =
        CityPointCached(
            name = type.name,
            point = type.point,
            placeId = type.placeId
        )
}