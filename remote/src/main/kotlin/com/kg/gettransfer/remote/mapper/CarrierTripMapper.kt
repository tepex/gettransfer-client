package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.CarrierTripEntity
import com.kg.gettransfer.data.model.PassengerAccountEntity

import com.kg.gettransfer.remote.model.CarrierTripModel

import org.koin.standalone.inject

/**
 * Map a [CarrierTripModel] from a [CarrierTripEntity] instance when data is moving between this later and the Data layer.
 */
open class CarrierTripMapper: EntityMapper<CarrierTripModel, CarrierTripEntity> {
    private val cityPointMapper: CityPointMapper by inject()
    private val vehicleBaseMapper: VehicleBaseMapper by inject()
    private val passengerAccountMapper: PassengerAccountMapper by inject()

    override fun fromRemote(type: CarrierTripModel) = 
        CarrierTripEntity(type.id,
                          type.transferId,
                          cityPointMapper.fromRemote(type.from),
                          cityPointMapper.fromRemote(type.to),
                          type.dateLocal,
                          type.duration,
                          type.distance,
                          type.time,
                          type.childSeats,
                          type.comment,
                          type.waterTaxi,
                          type.price,
                          vehicleBaseMapper.fromRemote(type.vehicle),
                          type.pax,
                          type.nameSign,
                          type.flightNumber,
                          type.paidSum,
                          type.remainToPay,
                          type.paidPercentage,
                          type.passengerAccount?.let { passengerAccountMapper.fromRemote(it) })
    
    override fun toRemote(type: CarrierTripEntity): CarrierTripModel { throw UnsupportedOperationException() }
}
