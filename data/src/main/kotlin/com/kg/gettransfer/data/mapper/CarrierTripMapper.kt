package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.CarrierTripEntity
import com.kg.gettransfer.data.model.CarrierTripVehicleEntity
import com.kg.gettransfer.data.model.PassengerAccountEntity

import com.kg.gettransfer.domain.model.CarrierTrip
import com.kg.gettransfer.domain.model.CarrierTripVehicle
import com.kg.gettransfer.domain.model.PassengerAccount

/**
 * Map a [CarrierTripEntity] to and from a [CarrierTrip] instance when data is moving between
 * this later and the Domain layer.
 */
open class CarrierTripMapper(private val cityPointMapper: CityPointMapper,
                             private val carrierVehicleMapper: CarrierVehicleMapper,
                             private val passengerAccountMapper: PassengerAccountMapper): Mapper<CarrierTripEntity, CarrierTrip> {
    /**
     * Map a [CarrierTripEntity] instance to a [CarrierTrip] instance.
     */
    override fun fromEntity(type: CarrierTripEntity): CarrierTrip {
        var passengerAccount: PassengerAccount? = null
        if(type.passengerAccount != null) passengerAccount = passengerAccountMapper.fromEntity(type.passengerAccount)
        return CarrierTrip(type.id,
                           type.transferId,
                           cityPointMapper.fromEntity(type.from),
                           cityPointMapper.fromEntity(type.to),
                           type.dateLocal,
                           type.duration,
                           type.distance,
                           type.time,
                           type.childSeats,
                           type.comment,
                           type.waterTaxi,
                           type.price,
                           carrierVehicleMapper.fromEntity(type.vehicle),
                           type.pax,
                           type.nameSign,
                           type.flightNumber,
                           type.paidSum,
                           type.remainToPay,
                           type.paidPercentage,
                           passengerAccount)
    }

    /**
     * Map a [CarrierTrip] instance to a [CarrierTripEntity] instance.
     */
    override fun toEntity(type: CarrierTrip): CarrierTripEntity {
        throw UnsupportedOperationException()
    }
}

open class CarrierVehicleMapper(): Mapper<CarrierTripVehicleEntity, CarrierTripVehicle> {
    override fun fromEntity(type: CarrierTripVehicleEntity) = CarrierTripVehicle(type.name, type.registrationNumber)
    override fun toEntity(type: CarrierTripVehicle) = CarrierTripVehicleEntity(type.name, type.registrationNumber) 
}

open class PassengerAccountMapper(private val userMapper: UserMapper): Mapper<PassengerAccountEntity, PassengerAccount> {
    override fun fromEntity(type: PassengerAccountEntity) = 
        PassengerAccount(userMapper.fromEntity(type.user), type.lastSeen)
    override fun toEntity(type: PassengerAccount) =
        PassengerAccountEntity(userMapper.toEntity(type.user), type.lastSeen) 
}
