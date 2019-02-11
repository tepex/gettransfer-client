package com.kg.gettransfer.cache.mapper

import com.kg.gettransfer.cache.model.VehicleInfoCached
import com.kg.gettransfer.data.model.VehicleInfoEntity

open class VehicleInfoEntityMapper : EntityMapper<VehicleInfoCached, VehicleInfoEntity> {
    override fun fromCached(type: VehicleInfoCached) =
            VehicleInfoEntity(
                    name               = type.name,
                    registrationNumber = type.registrationNumber
            )

    override fun toCached(type: VehicleInfoEntity) =
            VehicleInfoCached(
                    name               = type.name,
                    registrationNumber = type.registrationNumber
            )
}