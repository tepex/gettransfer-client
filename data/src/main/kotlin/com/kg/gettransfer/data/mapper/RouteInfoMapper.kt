package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.RouteInfoEntity

import com.kg.gettransfer.domain.model.RouteInfo
import com.kg.gettransfer.domain.model.TransportTypePrice

/**
 * Map a [RouteInfoEntity] to and from a [RouteInfo] instance when data is moving between this later and the Domain layer.
 */
open class RouteInfoMapper(): Mapper<RouteInfoEntity, RouteInfo> {
    /**
     * Map a [AccountEntity] instance to a [Account] instance
     */
    override fun fromEntity(type: RouteInfoEntity) =
        RouteInfo(type.success,
                  type.distance,
                  type.duration,
                  type.prices.map { TransportTypePrice(it.transferId, it.minFloat, it.min) },
                  type.watertaxi,
                  type.polyLines,
                  type.overviewPolyline)

    /**
     * Map a [RouteInfo] instance to a [RouteInfoEntity] instance.
     */
    override fun toEntity(type: RouteInfo): RouteInfoEntity { throw UnsupportedOperationException() }
}
