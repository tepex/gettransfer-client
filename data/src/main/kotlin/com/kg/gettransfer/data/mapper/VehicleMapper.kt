package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.VehicleEntity

import com.kg.gettransfer.domain.model.TransportType
import com.kg.gettransfer.domain.model.Vehicle

import org.koin.standalone.get

/**
 * Map a [VehicleEntity] to and from a [Vehicle] instance when data is moving between this later and the Domain layer.
 */
open class VehicleMapper : Mapper<VehicleEntity, Vehicle> {

    /**
     * Map a [VehicleEntity] instance to a [Vehicle] instance.
     */
    override fun fromEntity(type: VehicleEntity) =
        Vehicle(
            id                 = type.id,
            name               = type.name,
            registrationNumber = type.registrationNumber,
            year               = type.year,
            color              = type.color,
            transportType      = TransportType(
                id = TransportType.ID.parse(type.transportTypeId),
                paxMax = type.paxMax,
                luggageMax = type.luggageMax
            ),
            photos = type.photos
        )

    /**
     * Map a [Vehicle] instance to a [VehicleEntity] instance.
     */
    override fun toEntity(type: Vehicle) =
        VehicleEntity(
            id = type.id,
            name = type.name,
            registrationNumber = type.registrationNumber,
            year = type.year,
            color = type.color,
            transportTypeId = type.transportType.id.toString(),
            paxMax = type.transportType.paxMax,
            luggageMax = type.transportType.luggageMax,
            photos = type.photos
        )
}
