package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.CarrierTripEntity

import com.kg.gettransfer.domain.model.CarrierTrip
import com.kg.gettransfer.domain.model.PassengerAccount

import java.text.SimpleDateFormat

import java.util.Locale

import org.koin.standalone.inject

/**
 * Map a [CarrierTripEntity] to and from a [CarrierTrip] instance when data is moving between
 * this later and the Domain layer.
 */
open class CarrierTripMapper: Mapper<CarrierTripEntity, CarrierTrip> {
    private val cityPointMapper: CityPointMapper by inject()
    private val vehicleBaseMapper: VehicleBaseMapper by inject()
    private val passengerAccountMapper: PassengerAccountMapper by inject()
    /**
     * Map a [CarrierTripEntity] instance to a [CarrierTrip] instance.
     */
    override fun fromEntity(type: CarrierTripEntity) =
        CarrierTrip(type.id,
                    type.transferId,
                    cityPointMapper.fromEntity(type.from),
                    cityPointMapper.fromEntity(type.to),
                    SimpleDateFormat(Mapper.ISO_FORMAT_STRING, Locale.US).parse(type.dateLocal),
                    type.duration,
                    type.distance,
                    type.time,
                    type.childSeats,
                    type.comment,
                    type.waterTaxi,
                    type.price,
                    vehicleBaseMapper.fromEntity(type.vehicleBase),
                    type.pax,
                    type.nameSign,
                    type.flightNumber,
                    type.paidSum,
                    type.remainToPay,
                    type.paidPercentage,
                    type.passengerAccount?.let { passengerAccountMapper.fromEntity(it) })

    /**
     * Map a [CarrierTrip] instance to a [CarrierTripEntity] instance.
     */
    override fun toEntity(type: CarrierTrip): CarrierTripEntity { throw UnsupportedOperationException() }
}
