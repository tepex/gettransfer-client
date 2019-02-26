package com.kg.gettransfer.cache.mapper

import com.kg.gettransfer.cache.model.CarrierTripBaseCached
import com.kg.gettransfer.data.model.CarrierTripBaseEntity
import com.kg.gettransfer.data.model.CarrierTripEntity
import org.koin.standalone.KoinComponent
import org.koin.standalone.get

open class CarrierTripBaseEntityMapper : KoinComponent {
    private val cityPointMapper = get<CityPointEntityMapper>()
    private val vehicleInfoMapper = get<VehicleInfoEntityMapper>()

    fun fromCached(type: CarrierTripBaseCached) =
            CarrierTripBaseEntity(
                    id                    = type.id,
                    transferId            = type.transferId,
                    from                  = cityPointMapper.fromCached(type.from),
                    to                    = type.to?.let { cityPointMapper.fromCached(it) },
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
                    vehicle               = vehicleInfoMapper.fromCached(type.vehicle)
            )

    fun toCached(type: CarrierTripBaseEntity) =
            CarrierTripBaseCached(
                    id                    = type.id,
                    transferId            = type.transferId,
                    from                  = cityPointMapper.toCached(type.from),
                    to                    = type.to?.let { cityPointMapper.toCached(it) },
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
                    vehicle               = vehicleInfoMapper.toCached(type.vehicle)
            )

    fun toCached(type: CarrierTripEntity) =
            CarrierTripBaseCached(
                    id                    = type.id,
                    transferId            = type.transferId,
                    from                  = cityPointMapper.toCached(type.from),
                    to                    = type.to?.let { cityPointMapper.toCached(it) },
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
                    vehicle               = vehicleInfoMapper.toCached(type.vehicle)
            )
}