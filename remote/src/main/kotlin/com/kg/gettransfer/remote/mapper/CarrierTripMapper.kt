package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.CarrierTripEntity

import com.kg.gettransfer.remote.model.CarrierTripModel

import org.koin.standalone.get

/**
 * Map a [CarrierTripModel] from a [CarrierTripEntity] instance when data is moving between this later and the Data layer.
 */
open class CarrierTripMapper : EntityMapper<CarrierTripModel, CarrierTripEntity> {
    private val cityPointMapper        = get<CityPointMapper>()
    private val vehicleInfoMapper      = get<VehicleInfoMapper>()
    private val passengerAccountMapper = get<PassengerAccountMapper>()

    override fun fromRemote(type: CarrierTripModel) =
        CarrierTripEntity(
            id                    = type.id,
            transferId            = type.transferId,
            from                  = cityPointMapper.fromRemote(type.from),
            to                    = type.to?.let { cityPointMapper.fromRemote(it) },
            dateLocal             = type.dateLocal,
            duration              = type.duration,
            distance              = type.distance,
            time                  = type.time,
            childSeats            = type.childSeats,
            childSeatsInfant      = type.childSeatsInfant,
            childSeatsConvertible = type.childSeatsConvertible,
            childSeatsBooster     = type.childSeatsBooster,
            comment               = type.comment,
            waterTaxi             = type.waterTaxi,
            price                 = type.price,
            vehicle               = vehicleInfoMapper.fromRemote(type.vehicle),
            pax                   = type.pax,
            nameSign              = type.nameSign,
            flightNumber          = type.flightNumber,
            paidSum               = type.paidSum,
            remainsToPay          = type.remainsToPay,
            paidPercentage        = type.paidPercentage,
            passengerAccount      = passengerAccountMapper.fromRemote(type.passengerAccount)
        )

    override fun toRemote(type: CarrierTripEntity): CarrierTripModel { throw UnsupportedOperationException() }
}
