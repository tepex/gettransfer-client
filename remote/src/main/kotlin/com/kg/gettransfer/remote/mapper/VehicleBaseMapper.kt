package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.VehicleBaseEntity

import com.kg.gettransfer.remote.model.VehicleBaseModel

/**
 * Map a [VehicleBaseModel] from a [VehicleBaseEntity] instance when data is moving between this later and the Data layer.
 */
open class VehicleBaseMapper(): EntityMapper<VehicleBaseModel, VehicleBaseEntity> {
    override fun fromRemote(type: VehicleBaseModel) = VehicleBaseEntity(type.name, type.registrationNumber)
    override fun toRemote(type: VehicleBaseEntity) = VehicleBaseModel(type.name, type.registrationNumber)
}
