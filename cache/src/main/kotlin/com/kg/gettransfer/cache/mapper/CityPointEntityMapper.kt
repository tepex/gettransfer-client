package com.kg.gettransfer.cache.mapper

import com.kg.gettransfer.cache.model.CityPointCached

import com.kg.gettransfer.data.model.CityPointEntity

class CityPointEntityMapper: EntityMapper<CityPointCached, CityPointEntity> {
    override fun fromCached(type: CityPointCached) = CityPointEntity(type.name, type.point, type.placeId)
    override fun toCached(type: CityPointEntity) = CityPointCached(type.name, type.point, type.placeId)
}