package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.Vehicle

import com.kg.gettransfer.presentation.model.VehicleModel

import org.koin.standalone.KoinComponent
import org.koin.standalone.get

open class VehicleMapper : KoinComponent {
    private val transportTypeMapper = get<TransportTypeMapper>()

    fun toView(type: Vehicle) =
        VehicleModel(
            id                 = type.id,
            name               = type.name,
            registrationNumber = type.registrationNumber,
            year               = type.year,
            color              = type.color,
            transportType      = transportTypeMapper.toView(type.transportType),
            photos             = type.photos
        )
}
