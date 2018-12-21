package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.CarrierTripBaseEntity

import com.kg.gettransfer.remote.model.CarrierTripBaseModel

import org.koin.standalone.get

/**
 * Map a [CarrierTripBaseModel] from a [CarrierTripBaseEntity] instance when data is moving between this later and the Data layer.
 */
open class CarrierTripBaseMapper : EntityMapper<CarrierTripBaseModel, CarrierTripBaseEntity> {
    private val cityPointMapper   = get<CityPointMapper>()
    private val vehicleInfoMapper = get<VehicleInfoMapper>()

    override fun fromRemote(type: CarrierTripBaseModel) =
        CarrierTripBaseEntity(
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
            vehicle               = vehicleInfoMapper.fromRemote(type.vehicle)
        )

    override fun toRemote(type: CarrierTripBaseEntity): CarrierTripBaseModel { throw UnsupportedOperationException() }
}
