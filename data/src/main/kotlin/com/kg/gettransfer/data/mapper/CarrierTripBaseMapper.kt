package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.CarrierTripBaseEntity
import com.kg.gettransfer.data.model.map

import com.kg.gettransfer.domain.model.CarrierTripBase
import com.kg.gettransfer.domain.model.CityPoint

import java.text.DateFormat

import org.koin.standalone.get


/**
 * Map a [CarrierTripBaseEntity] to and from a [CarrierTripBase] instance when data is moving between
 * this later and the Domain layer.
 */
open class CarrierTripBaseMapper : Mapper<CarrierTripBaseEntity, CarrierTripBase> {
    private val vehicleInfoMapper      = get<VehicleInfoMapper>()
    private val dateFormat             = get<ThreadLocal<DateFormat>>("iso_date")

    /**
     * Map a [CarrierTripBaseEntity] instance to a [CarrierTripBase] instance.
     */
    override fun fromEntity(type: CarrierTripBaseEntity) =
        CarrierTripBase(
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
            vehicle               = vehicleInfoMapper.fromEntity(type.vehicle)
        )

    /**
     * Map a [CarrierTripBase] instance to a [CarrierTripBaseEntity] instance.
     */
    override fun toEntity(type: CarrierTripBase): CarrierTripBaseEntity { throw UnsupportedOperationException() }
}
