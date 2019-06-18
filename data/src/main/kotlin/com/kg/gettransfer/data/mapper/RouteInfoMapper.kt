package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.RouteInfoEntity

import com.kg.gettransfer.domain.model.RouteInfo
import com.kg.gettransfer.domain.model.TransportType
import com.kg.gettransfer.domain.model.TransportTypePrice

import org.koin.standalone.get

/**
 * Map a [RouteInfoEntity] to and from a [RouteInfo] instance when data is moving between this later and the Domain layer.
 */
open class RouteInfoMapper : Mapper<RouteInfoEntity, RouteInfo> {
    private val transportTypePriceMapper = get<TransportTypePriceMapper>()
    /**
     * Map a [AccountEntity] instance to a [Account] instance
     */
    override fun fromEntity(type: RouteInfoEntity) =
        RouteInfo(
            success          = type.success,
            distance         = type.distance,
            duration         = type.duration,
            prices           = type.prices.entries.associate { TransportType.ID.parse(it.key) to transportTypePriceMapper.fromEntity(it.value) },
            watertaxi        = type.watertaxi,
            polyLines        = type.polyLines,
            overviewPolyline = type.overviewPolyline,
            hintsToComments = type.hintsToComments
        )

    /**
     * Map a [RouteInfo] instance to a [RouteInfoEntity] instance.
     */
    override fun toEntity(type: RouteInfo): RouteInfoEntity { throw UnsupportedOperationException() }
}
