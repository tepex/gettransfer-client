package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.CityPointEntity

import com.kg.gettransfer.remote.model.CityPointModel

/**
 * Map a [CityPointModel] from a [CityPointEntity] instance when data is moving between this later and the Data layer.
 */
open class CityPointMapper(): EntityMapper<CityPointModel, CityPointEntity> {

    override fun fromRemote(type: CityPointModel) = CityPointEntity(type.name, type.point, type.placeId)
    override fun toRemote(type: CityPointEntity): CityPointModel(type.name, type.point, type.placeId)
}
