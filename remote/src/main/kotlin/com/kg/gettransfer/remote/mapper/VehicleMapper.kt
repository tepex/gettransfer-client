package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.TransportTypeEntity
import com.kg.gettransfer.data.model.VehicleBaseEntity
import com.kg.gettransfer.data.model.VehicleEntity

import com.kg.gettransfer.remote.model.VehicleModel

/**
 * Map a [VehicleModel] from an [VehicleEntity] instance when data is moving between this later and the Data layer.
 */
open class VehicleMapper : EntityMapper<VehicleModel, VehicleEntity> {
    override fun fromRemote(type: VehicleModel) =
        VehicleEntity(
            type.id,
            type.name,
            type.registrationNumber,
            type.year,
            type.color,
            TransportTypeEntity(type.transportTypeId, type.paxMax, type.luggageMax),
            type.photos
        )

    override fun toRemote(type: VehicleEntity): VehicleModel { throw UnsupportedOperationException() }
}
