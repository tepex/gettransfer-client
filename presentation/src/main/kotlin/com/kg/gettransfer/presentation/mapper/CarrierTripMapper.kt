package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.CarrierTrip
import com.kg.gettransfer.domain.model.CarrierTripBase

import com.kg.gettransfer.presentation.model.CarrierTripModel
import com.kg.gettransfer.presentation.model.TotalPriceModel

import java.util.Calendar

import org.koin.core.get

open class CarrierTripMapper : Mapper<CarrierTripModel, CarrierTrip> {
    private val carrierTripBaseMapper  = get<CarrierTripBaseMapper>()
    private val passengerAccountMapper = get<PassengerAccountMapper>()

    override fun toView(type: CarrierTrip) =
        CarrierTripModel(
            base              = carrierTripBaseMapper.toView(type.base),
            countPassengers   = type.pax,
            nameSign          = type.nameSign,
            flightNumber      = type.flightNumber,
            paidSum           = type.paidSum,
            totalPrice        = type.remainsToPay?.let { TotalPriceModel(it, type.paidPercentage!!) },
            passenger         = type.passengerAccount?.let { passengerAccountMapper.toView(it) },
            showPassengerInfo = allowPassengerInfo(type.base)
        )

    override fun fromView(type: CarrierTripModel): CarrierTrip { throw UnsupportedOperationException() }

    companion object {
        private const val HOURS_TO_SHOWING_PASSENGER_INFO = 24

        private fun allowPassengerInfo(carrierTrip: CarrierTripBase): Boolean {
            val calendar = Calendar.getInstance()
            calendar.apply {
                time = carrierTrip.dateLocal
                add(Calendar.MINUTE, carrierTrip.time ?: carrierTrip.duration?.times(60) ?: 0)
                add(Calendar.MINUTE, HOURS_TO_SHOWING_PASSENGER_INFO.times(60))
            }
            return calendar.time.after(Calendar.getInstance().time)
        }
    }
}
