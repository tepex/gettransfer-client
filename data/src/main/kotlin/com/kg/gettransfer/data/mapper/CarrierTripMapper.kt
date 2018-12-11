package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.CarrierTripEntity

import com.kg.gettransfer.domain.model.CarrierTrip
import com.kg.gettransfer.domain.model.PassengerAccount

import java.text.DateFormat

import org.koin.standalone.get

/**
 * Map a [CarrierTripEntity] to and from a [CarrierTrip] instance when data is moving between
 * this later and the Domain layer.
 */
open class CarrierTripMapper : Mapper<CarrierTripEntity, CarrierTrip> {
    private val cityPointMapper        = get<CityPointMapper>()
    private val vehicleBaseMapper      = get<VehicleBaseMapper>()
    private val passengerAccountMapper = get<PassengerAccountMapper>()
    private val dateFormat             = get<ThreadLocal<DateFormat>>("iso_date")

    /**
     * Map a [CarrierTripEntity] instance to a [CarrierTrip] instance.
     */
    override fun fromEntity(type: CarrierTripEntity) =
        CarrierTrip(
            type.id,
            type.transferId,
            cityPointMapper.fromEntity(type.from),
            cityPointMapper.fromEntity(type.to),
            dateFormat.get().parse(type.dateLocal),
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
            type.passengerAccount?.let { passengerAccountMapper.fromEntity(it) }
        )

    /**
     * Map a [CarrierTrip] instance to a [CarrierTripEntity] instance.
     */
    override fun toEntity(type: CarrierTrip): CarrierTripEntity { throw UnsupportedOperationException() }
}
