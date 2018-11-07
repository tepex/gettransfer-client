package com.kg.gettransfer.cache.mapper

import com.kg.gettransfer.cache.model.CityPointCached
import com.kg.gettransfer.data.model.CityPointEntity

class CityPointEntityMapper : EntityMapper<CityPointCached, CityPointEntity> {
    override fun mapFromCached(type: CityPointCached): CityPointEntity = CityPointEntity(type.name, type.point, type.placeId)

    override fun mapToCached(type: CityPointEntity): CityPointCached = CityPointCached(type.name, type.point, type.placeId)
}