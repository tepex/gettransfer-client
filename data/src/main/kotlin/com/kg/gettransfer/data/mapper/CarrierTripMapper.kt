package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.CarrierTripEntity
import com.kg.gettransfer.data.model.map

import com.kg.gettransfer.domain.model.CarrierTrip
import com.kg.gettransfer.domain.model.CarrierTripBase
import com.kg.gettransfer.domain.model.CityPoint

import java.text.DateFormat

import org.koin.standalone.get

/**
 * Map a [CarrierTripEntity] to and from a [CarrierTrip] instance when data is moving between
 * this later and the Domain layer.
 */
open class CarrierTripMapper : Mapper<CarrierTripEntity, CarrierTrip> {
    private val dateFormat = get<ThreadLocal<DateFormat>>("iso_date")

    /**
     * Map a [CarrierTripEntity] instance to a [CarrierTrip] instance.
     */
    override fun fromEntity(type: CarrierTripEntity) =
        CarrierTrip(
            base = CarrierTripBase(
                id                    = type.id,
                transferId            = type.transferId,
                from                  = type.from.map(),
                to                    = type.to?.let { it.map() } ?: CityPoint.EMPTY,
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
                vehicle               = type.vehicle.map()
            ),
            pax              = type.pax,
            nameSign         = type.nameSign,
            flightNumber     = type.flightNumber,
            paidSum          = type.paidSum,
            remainsToPay     = type.remainsToPay,
            paidPercentage   = type.paidPercentage,
            passengerAccount = type.passengerAccount?.let { it.map(dateFormat.get()) }
        )

    /**
     * Map a [CarrierTrip] instance to a [CarrierTripEntity] instance.
     */
    override fun toEntity(type: CarrierTrip): CarrierTripEntity { throw UnsupportedOperationException() }
}
