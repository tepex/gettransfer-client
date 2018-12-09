package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.Vehicle

import com.kg.gettransfer.presentation.model.VehicleBaseModel
import com.kg.gettransfer.presentation.model.VehicleModel

open class VehicleMapper : Mapper<VehicleModel, Vehicle> {
    override fun toView(type: Vehicle) =
        VehicleModel(
            VehicleBaseModel(type.vehicleBase.name, type.vehicleBase.registrationNumber),
            type.year,
            type.color,
            TransportTypeModel(type.transportType, null),
            type.photos
        )

    override fun fromView(type: VehicleModel): Vehicle { throw UnsupportedOperationException() }
}
