package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.RouteInfoEntity
import com.kg.gettransfer.data.model.TransportTypePriceEntity

import com.kg.gettransfer.remote.model.RouteInfoModel

/**
 * Map a [RouteInfoModel] from a [RouteInfoEntity] instance when data is moving between this later and the Data layer.
 */
open class RouteInfoMapper(): EntityMapper<RouteInfoModel, RouteInfoEntity> {
    override fun fromRemote(type: RouteInfoModel) =
        RouteInfoEntity(type.success,
                        type.distance,
                        type.duration,
                        type.prices?.map { TransportTypePriceEntity(it.key, it.value.minFloat, it.value.min, it.value.max) } ?: emptyList<TransportTypePriceEntity>(),
                        type.watertaxi,
                        type.routes?.first()?.legs?.first()?.steps?.map { it.polyline.points } ?: emptyList<String>(),
                        type.routes?.first()?.overviewPolyline?.points)
    
    override fun toRemote(type: RouteInfoEntity): RouteInfoModel { throw UnsupportedOperationException() }
}
