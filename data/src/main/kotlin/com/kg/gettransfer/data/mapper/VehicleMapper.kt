package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.VehicleEntity

import com.kg.gettransfer.domain.model.Vehicle

/**
 * Map a [VehicleEntity] to and from a [Vehicle] instance when data is moving between this later and the Domain layer.
 */
open class VehicleMapper(): Mapper<VehicleEntity, Vehicle> {
    /**
     * Map a [VehicleEntity] instance to a [Vehicle] instance.
     */
    override fun fromEntity(type: VehicleEntity) = 
        Vehicle(type.name,
                type.registrationNumber,
                type.year,
                type.color,
                type.transportTypeId,
                type.paxMax,
                type.luggageMax,
                type.photos)
    /**
     * Map a [Vehicle] instance to a [VehicleEntity] instance.
     */
    override fun toEntity(type: Vehicle) = 
        VehicleEntity(type.name,
                      type.registrationNumber,
                      type.year,
                      type.color,
                      type.transportTypeId,
                      type.paxMax,
                      type.luggageMax,
                      type.photos)
}
