package com.kg.gettransfer.cache.mapper

import com.kg.gettransfer.cache.model.CarrierTripBaseCached
import com.kg.gettransfer.cache.model.CarrierTripMoreCached
import com.kg.gettransfer.cache.model.CarrierTripCached
import com.kg.gettransfer.cache.model.PassengerAccountCached
import com.kg.gettransfer.data.model.CarrierTripEntity
import com.kg.gettransfer.data.model.PassengerAccountEntity
import org.koin.standalone.KoinComponent
import org.koin.standalone.get

open class CarrierTripEntityMapper : KoinComponent {
    private val carrierTripBaseMapper = get<CarrierTripBaseEntityMapper>()
    private val passengerAccountMapper = get<PassengerAccountEntityMapper>()
    private val cityPointMapper = get<CityPointEntityMapper>()
    private val vehicleInfoMapper = get<VehicleInfoEntityMapper>()

    fun fromCached(typeBase: CarrierTripBaseCached, typeMore: CarrierTripMoreCached?) =
            CarrierTripEntity(
                    id                    = typeBase.id,
                    transferId            = typeBase.transferId,
                    from                  = cityPointMapper.fromCached(typeBase.from),
                    to                    = typeBase.to?.let { cityPointMapper.fromCached(it) },
                    dateLocal             = typeBase.dateLocal,
                    duration              = typeBase.duration,
                    distance              = typeBase.distance,
                    time                  = typeBase.time,
                    childSeats            = typeBase.childSeats,
                    childSeatsInfant      = typeBase.childSeatsInfant,
                    childSeatsConvertible = typeBase.childSeatsConvertible,
                    childSeatsBooster     = typeBase.childSeatsBooster,
                    comment               = typeBase.comment,
                    waterTaxi             = typeBase.waterTaxi,
                    price                 = typeBase.price,
                    vehicle               = vehicleInfoMapper.fromCached(typeBase.vehicle),
                    pax                   = typeMore?.pax,
                    nameSign              = typeMore?.nameSign,
                    flightNumber          = typeMore?.flightNumber,
                    paidSum               = typeMore?.paidSum,
                    remainsToPay          = typeMore?.remainsToPay,
                    paidPercentage        = typeMore?.paidPercentage,
                    passengerAccount      = typeMore?.passengerAccount?.let { passengerAccountMapper.fromCached(it) }
            )

    fun toCached(type: CarrierTripEntity) =
            CarrierTripCached(
                    base = carrierTripBaseMapper.toCached(type),
                    more = CarrierTripMoreCached(
                            id               = type.id,
                            pax              = type.pax!!,
                            nameSign         = type.nameSign,
                            flightNumber     = type.flightNumber,
                            paidSum          = type.paidSum!!,
                            remainsToPay     = type.remainsToPay!!,
                            paidPercentage   = type.paidPercentage!!,
                            passengerAccount = passengerAccountMapper.toCached(type.passengerAccount!!)
                    )
            )
}

open class PassengerAccountEntityMapper : EntityMapper<PassengerAccountCached, PassengerAccountEntity> {
    private val profileMapper = get<ProfileEntityMapper>()

    override fun fromCached(type: PassengerAccountCached) =
            PassengerAccountEntity(
                    id       = type.id,
                    profile  = profileMapper.fromCached(type.profile),
                    lastSeen = type.lastSeen
            )

    override fun toCached(type: PassengerAccountEntity) =
            PassengerAccountCached(
                    id       = type.id,
                    profile  = profileMapper.toCached(type.profile),
                    lastSeen = type.lastSeen
            )
}