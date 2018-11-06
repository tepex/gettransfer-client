package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.VehicleEntity

import com.kg.gettransfer.domain.model.TransportType
import com.kg.gettransfer.domain.model.Vehicle
import com.kg.gettransfer.domain.model.VehicleBase

/**
 * Map a [VehicleEntity] to and from a [Vehicle] instance when data is moving between this later and the Domain layer.
 */
open class VehicleMapper(): Mapper<VehicleEntity, Vehicle> {
    /**
     * Map a [VehicleEntity] instance to a [Vehicle] instance.
     */
    override fun fromEntity(type: VehicleEntity) = 
        Vehicle(VehicleBase(type.name, type.registration_number),
                type.year,
                type.color,
                TransportType(type.transport_type_id, type.pax_max, type.luggage_max),
                type.photos)
    /**
     * Map a [Vehicle] instance to a [VehicleEntity] instance.
     */
    override fun toEntity(type: Vehicle) = 
        VehicleEntity(type.vehicleBase.name,
                      type.vehicleBase.registrationNumber,
                      type.year,
                      type.color,
                      type.transportType.id,
                      type.transportType.paxMax,
                      type.transportType.luggageMax,
                      type.photos)
}
