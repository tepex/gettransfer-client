package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.CarrierTrip

import com.kg.gettransfer.presentation.model.CarrierTripModel
import com.kg.gettransfer.presentation.model.TotalPriceModel

import com.kg.gettransfer.presentation.ui.SystemUtils

import java.util.Calendar

import org.koin.standalone.get

open class CarrierTripMapper : Mapper<CarrierTripModel, CarrierTrip> {
    private val carrierTripBaseMapper  = get<CarrierTripBaseMapper>()
    private val passengerAccountMapper = get<PassengerAccountMapper>()

    override fun toView(type: CarrierTrip) =
        CarrierTripModel(
            base            = carrierTripBaseMapper.toView(type.base),
            countPassengers = type.pax,
            nameSign        = type.nameSign,
            flightNumber    = type.flightNumber,
            paidSum         = type.paidSum,
            totalPrice      = type.remainsToPay?.let { TotalPriceModel(it, type.paidPercentage!!) },
            passenger       = type.passengerAccount?.let { passengerAccountMapper.toView(it) }
        )

    override fun fromView(type: CarrierTripModel): CarrierTrip { throw UnsupportedOperationException() }
}
