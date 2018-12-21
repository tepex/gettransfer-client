package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.VehicleInfoEntity

import com.kg.gettransfer.remote.model.VehicleInfoModel

/**
 * Map a [VehicleInfoModel] from a [VehicleInfoEntity] instance when data is moving between this later and the Data layer.
 */
open class VehicleInfoMapper : EntityMapper<VehicleInfoModel, VehicleInfoEntity> {
    override fun fromRemote(type: VehicleInfoModel) =
        VehicleInfoEntity(
            name = type.name,
            registrationNumber = type.registrationNumber
        )

    override fun toRemote(type: VehicleInfoEntity) =
        VehicleInfoModel(
            name = type.name,
            registrationNumber = type.registrationNumber
        )
}
