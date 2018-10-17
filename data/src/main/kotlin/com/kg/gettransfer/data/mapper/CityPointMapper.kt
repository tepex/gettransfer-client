package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.mapper.PointMapper
import com.kg.gettransfer.data.model.CityPointEntity

import com.kg.gettransfer.domain.model.CityPoint

/**
 * Map a [CityPointEntity] to and from a [CityPoint] instance when data is moving between
 * this later and the Domain layer.
 */
open class CityPointMapper(private val pointMapper: PointMapper): Mapper<CityPointEntity, CityPoint> {
    override fun fromEntity(type: CityPointEntity) = CityPoint(type.name, pointMapper.fromEntity(type.point), type.placeId)
    override fun toEntity(type: CityPoint) = CityPointEntity(type.name!!, pointMapper.toEntity(type.point!!), type.placeId)
}
