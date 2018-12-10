package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.Vehicle

import com.kg.gettransfer.presentation.model.VehicleBaseModel
import com.kg.gettransfer.presentation.model.VehicleModel

import org.koin.standalone.get

open class VehicleMapper : Mapper<VehicleModel, Vehicle> {
    private val transportTypeMapper = get<TransportTypeMapper>()
    private val vehicleBaseMapper = get<VehicleBaseMapper>()

    override fun toView(type: Vehicle) =
        VehicleModel(
            vehicleBaseMapper.toView(type.vehicleBase),
            type.year,
            type.color,
            transportTypeMapper.toView(type.transportType),
            type.photos
        )

    override fun fromView(type: VehicleModel): Vehicle { throw UnsupportedOperationException() }
}
