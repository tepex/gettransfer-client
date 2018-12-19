package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.CarrierTripEntity

import com.kg.gettransfer.domain.model.CarrierTrip
import com.kg.gettransfer.domain.model.CarrierTripBase
import com.kg.gettransfer.domain.model.PassengerAccount

import java.text.DateFormat

import org.koin.standalone.get

/**
 * Map a [CarrierTripEntity] to and from a [CarrierTrip] instance when data is moving between
 * this later and the Domain layer.
 */
open class CarrierTripMapper : Mapper<CarrierTripEntity, CarrierTrip> {
    private val cityPointMapper        = get<CityPointMapper>()
    private val vehicleInfoMapper      = get<VehicleInfoMapper>()
    private val passengerAccountMapper = get<PassengerAccountMapper>()
    private val dateFormat             = get<ThreadLocal<DateFormat>>("iso_date")

    /**
     * Map a [CarrierTripEntity] instance to a [CarrierTrip] instance.
     */
    override fun fromEntity(type: CarrierTripEntity) =
        CarrierTrip(
            base = CarrierTripBase(
                id                    = type.id,
                transferId            = type.transferId,
                from                  = cityPointMapper.fromEntity(type.from),
                to                    = type.to?.let { cityPointMapper.fromEntity(it) },
                dateLocal             = dateFormat.get().parse(type.dateLocal),
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
                vehicle               = vehicleInfoMapper.fromEntity(type.vehicle)
            ),
            pax              = type.pax,
            nameSign         = type.nameSign,
            flightNumber     = type.flightNumber,
            paidSum          = type.paidSum,
            remainsToPay     = type.remainsToPay,
            paidPercentage   = type.paidPercentage,
            passengerAccount = passengerAccountMapper.fromEntity(type.passengerAccount)
        )

    /**
     * Map a [CarrierTrip] instance to a [CarrierTripEntity] instance.
     */
    override fun toEntity(type: CarrierTrip): CarrierTripEntity { throw UnsupportedOperationException() }
}
