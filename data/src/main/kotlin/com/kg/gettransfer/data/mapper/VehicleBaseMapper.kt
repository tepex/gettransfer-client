package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.VehicleBaseEntity

import com.kg.gettransfer.domain.model.VehicleBase

/**
 * Map a [VehicleBaseEntity] to and from a [VehicleBase] instance when data is moving between
 * this later and the Domain layer.
 */
open class VehicleBaseMapper(): Mapper<VehicleBaseEntity, VehicleBase> {
    /**
     * Map a [VehicleBaseEntity] instance to a [VehicleBase] instance.
     */
    override fun fromEntity(type: VehicleBaseEntity) = VehicleBase(type.name, type.registrationNumber)
    /**
     * Map a [VehicleBase] instance to a [VehicleBaseEntity] instance.
     */
    override fun toEntity(type: VehicleBase) = VehicleBaseEntity(type.name, type.registrationNumber)
}
