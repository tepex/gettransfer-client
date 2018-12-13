package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.CarrierTrip

import com.kg.gettransfer.presentation.model.CarrierTripModel

import com.kg.gettransfer.presentation.ui.SystemUtils

open class CarrierTripMapper : Mapper<CarrierTripModel, CarrierTrip> {
    override fun toView(type: CarrierTrip) =
        CarrierTripModel(
            type.id,
            type.transferId,
            type.from.name!!,
            type.to.name!!,
            SystemUtils.formatDateTime(type.dateLocal),
            type.distance ?: Mapper.checkDistance(type.from.point!!, type.to.point!!),
            type.childSeats,
            type.comment,
            type.price,
            type.vehicle.name,
            type.pax,
            type.nameSign,
            type.flightNumber,
            type.remainToPay
        )

    override fun fromView(type: CarrierTripModel): CarrierTrip { throw UnsupportedOperationException() }
}
