package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.RouteInfoEntity
import com.kg.gettransfer.data.model.TransportTypePriceEntity

import com.kg.gettransfer.remote.model.RouteInfoModel

import org.koin.standalone.get

/**
 * Map a [RouteInfoModel] from a [RouteInfoEntity] instance when data is moving between this later and the Data layer.
 */
open class RouteInfoMapper : EntityMapper<RouteInfoModel, RouteInfoEntity> {
    private val transportTypePriceMapper = get<TransportTypePriceMapper>()

    override fun fromRemote(type: RouteInfoModel) =
        RouteInfoEntity(
            success = type.success,
            distance = type.distance,
            duration = type.duration,
            prices = type.prices?.mapValues { transportTypePriceMapper.fromRemote(it) } ?: emptyMap(),
            watertaxi = type.watertaxi,
            polyLines = type.routes?.first()?.legs?.first()?.steps?.map { it.polyline.points } ?: emptyList(),
            overviewPolyline = type.routes?.first()?.overviewPolyline?.points,
            hintsToComments = type.hintsToComments
        )

    override fun toRemote(type: RouteInfoEntity): RouteInfoModel { throw UnsupportedOperationException() }
}
