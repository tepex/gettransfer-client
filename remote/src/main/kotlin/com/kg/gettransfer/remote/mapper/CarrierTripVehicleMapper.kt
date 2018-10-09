package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.CarrierTripVehicleEntity

import com.kg.gettransfer.remote.model.CarrierTripVehicleModel

/**
 * Map a [CarrierTripVehicleModel] from a [CarrierTripVehicleEntity] instance when data is moving between this later and the Data layer.
 */
open class CarrierTripVehicleMapper(): EntityMapper<CarrierTripVehicleModel, CarrierTripVehicleEntity> {

    override fun fromRemote(type: CarrierTripVehicleModel) = CarrierTripVehicleEntity(type.name, type.registrationNumber)
    override fun toRemote(type: CarrierTripVehicleEntity) = CarrierTripVehicleModel(type.name, type.registrationNumber)
}
