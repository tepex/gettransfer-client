package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.CityPointEntity
import com.kg.gettransfer.domain.model.CityPoint

/**
 * Map a [CityPointEntity] to and from a [CityPoint] instance when data is moving between
 * this later and the Domain layer.
 */
open class CityPointMapper(): Mapper<CityPointEntity, CityPoint> {
    override fun fromEntity(type: CityPointEntity) = CityPoint(type.name, type.point, type.placeId)
    override fun toEntity(type: CityPoint) = CityPointEntity(type.name, type.point, type.placeId)
}
