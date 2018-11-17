package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.RouteInfoEntity
import com.kg.gettransfer.data.model.TransportTypePriceEntity

import com.kg.gettransfer.remote.model.RouteInfoModel

import org.koin.standalone.get

/**
 * Map a [RouteInfoModel] from a [RouteInfoEntity] instance when data is moving between this later and the Data layer.
 */
open class RouteInfoMapper(): EntityMapper<RouteInfoModel, RouteInfoEntity> {
    private val transportTypePriceMapper = get<TransportTypePriceMapper>()

    override fun fromRemote(type: RouteInfoModel) =
        RouteInfoEntity(type.success,
                        type.distance,
                        type.duration,
                        //type.prices?.map { TransportTypePriceEntity(it.key, it.value.minFloat, it.value.min, it.value.max) } ?: emptyList<TransportTypePriceEntity>(),
                        type.prices?.map { transportTypePriceMapper.fromRemote(it.value.apply { transferId = it.key }) } ?: emptyList<TransportTypePriceEntity>(),
                        type.watertaxi,
                        type.routes?.first()?.legs?.first()?.steps?.map { it.polyline.points } ?: emptyList<String>(),
                        type.routes?.first()?.overviewPolyline?.points)

    override fun toRemote(type: RouteInfoEntity): RouteInfoModel { throw UnsupportedOperationException() }
}
