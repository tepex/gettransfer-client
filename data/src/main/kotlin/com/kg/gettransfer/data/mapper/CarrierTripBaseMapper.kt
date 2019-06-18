package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.CarrierTripBaseEntity

import com.kg.gettransfer.domain.model.CarrierTripBase

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
            to                    = type.to?.let { it.map() },
            dateLocal             = dateFormat.get().parse(type.dateLocal),
            duration              = type.duration ?: CarrierTripBase.NO_DURATION,
            distance              = type.distance ?: CarrierTripBase.NO_DISTANCE,
            time                  = type.time ?: CarrierTripBase.NO_TIME,
            childSeats            = type.childSeats,
            childSeatsInfant      = type.childSeatsInfant,
            childSeatsConvertible = type.childSeatsConvertible,
            childSeatsBooster     = type.childSeatsBooster,
            comment               = type.comment ?: CarrierTripBase.NO_COMMENT,
            waterTaxi             = type.waterTaxi,
            price                 = type.price,
            vehicle               = vehicleInfoMapper.fromEntity(type.vehicle)
        )

    /**
     * Map a [CarrierTripBase] instance to a [CarrierTripBaseEntity] instance.
     */
    override fun toEntity(type: CarrierTripBase): CarrierTripBaseEntity { throw UnsupportedOperationException() }
}
