package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.VehicleEntity

import com.kg.gettransfer.domain.model.TransportType
import com.kg.gettransfer.domain.model.Vehicle
import com.kg.gettransfer.domain.model.VehicleBase

import org.koin.standalone.get

/**
 * Map a [VehicleEntity] to and from a [Vehicle] instance when data is moving between this later and the Domain layer.
 */
open class VehicleMapper : Mapper<VehicleEntity, Vehicle> {
    private val vehicleBaseMapper   = get<VehicleBaseMapper>()
    private val transportTypeMapper = get<TransportTypeMapper>()

    /**
     * Map a [VehicleEntity] instance to a [Vehicle] instance.
     */
    override fun fromEntity(type: VehicleEntity) =
        Vehicle(
            type.id,
            VehicleBase(type.name, type.registrationNumber),
            type.year,
            type.color,
            TransportType(type.transportTypeId, type.paxMax, type.luggageMax),
            type.photos
        )

    /**
     * Map a [Vehicle] instance to a [VehicleEntity] instance.
     */
    override fun toEntity(type: Vehicle) =
        VehicleEntity(
            type.id,
            type.vehicleBase.name,
            type.vehicleBase.registrationNumber,
            type.year,
            type.color,
            type.transportType.id,
            type.transportType.paxMax,
            type.transportType.luggageMax,
            type.photos
        )
}
