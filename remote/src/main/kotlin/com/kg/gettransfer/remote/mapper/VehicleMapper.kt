package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.TransportTypeEntity
import com.kg.gettransfer.data.model.VehicleEntity

import com.kg.gettransfer.remote.model.VehicleModel

/**
 * Map a [VehicleModel] from an [VehicleEntity] instance when data is moving between this later and the Data layer.
 */
open class VehicleMapper : EntityMapper<VehicleModel, VehicleEntity> {
    override fun fromRemote(type: VehicleModel) =
        VehicleEntity(
            id                 = type.id,
            name               = type.name,
            registrationNumber = type.registrationNumber,
            year               = type.year,
            color              = type.color,
            transportTypeId    = type.transportTypeId,
            paxMax             = type.paxMax,
            luggageMax         = type.luggageMax,
            photos             = type.photos
        )

    override fun toRemote(type: VehicleEntity): VehicleModel { throw UnsupportedOperationException() }
}
