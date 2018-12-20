package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.VehicleInfoEntity

import com.kg.gettransfer.domain.model.VehicleInfo

/**
 * Map a [VehicleInfoEntity] to and from a [VehicleInfo] instance when data is moving between
 * this later and the Domain layer.
 */
open class VehicleInfoMapper : Mapper<VehicleInfoEntity, VehicleInfo> {
    /**
     * Map a [VehicleInfoEntity] instance to a [VehicleInfo] instance.
     */
    override fun fromEntity(type: VehicleInfoEntity) =
        VehicleInfo(
            name = type.name,
            registrationNumber = type.registrationNumber
        )

    /**
     * Map a [VehicleInfo] instance to a [VehicleInfoEntity] instance.
     */
    override fun toEntity(type: VehicleInfo) =
        VehicleInfoEntity(
            name = type.name,
            registrationNumber = type.registrationNumber
        )
}
