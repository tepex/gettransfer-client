package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.CarrierTripVehicleEntity

import com.kg.gettransfer.domain.model.CarrierTripVehicle

/**
 * Map a [CarrierTripVehicleEntity] to and from a [CarrierTripVehicle] instance when data is moving between this later and the Domain layer.
 */
open class CarrierVehicleMapper(): Mapper<CarrierTripVehicleEntity, CarrierTripVehicle> {
    override fun fromEntity(type: CarrierTripVehicleEntity) = CarrierTripVehicle(type.name, type.registrationNumber)
    override fun toEntity(type: CarrierTripVehicle) = CarrierTripVehicleEntity(type.name, type.registrationNumber) 
}
