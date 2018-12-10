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
            vehicleBaseMapper.fromEntity(type.vehicleBase),
            type.year,
            type.color,
            transportTypeMapper.fromEntity(type.transportType),
            type.photos
        )

    /**
     * Map a [Vehicle] instance to a [VehicleEntity] instance.
     */
    override fun toEntity(type: Vehicle) =
        VehicleEntity(
            vehicleBaseMapper.toEntity(type.vehicleBase),
            type.year,
            type.color,
            transportTypeMapper.toEntity(type.transportType),
            type.photos
        )
}
