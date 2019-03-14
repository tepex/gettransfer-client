package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.CarrierTripBase

import com.kg.gettransfer.presentation.model.CarrierTripBaseModel

import com.kg.gettransfer.presentation.ui.SystemUtils

import java.util.Calendar

import org.koin.standalone.get

open class CarrierTripBaseMapper : Mapper<CarrierTripBaseModel, CarrierTripBase> {
    private val vehicleInfoMapper = get<VehicleInfoMapper>()

    override fun toView(type: CarrierTripBase): CarrierTripBaseModel {
        val tr = ((type.dateLocal.time - Calendar.getInstance().timeInMillis) / 60_000).toInt()
        return CarrierTripBaseModel(
            id                    = type.id,
            transferId            = type.transferId,
            from                  = type.from.name!!,
            to                    = type.to?.name,
            dateLocal             = type.dateLocal,
            duration              = type.duration,
            distance              = type.distance ?: Mapper.checkDistance(type.from.point!!, type.to?.point),
            time                  = type.time,
            countChild            = type.childSeats,
            childSeatsInfant      = type.childSeatsInfant,
            childSeatsConvertible = type.childSeatsConvertible,
            childSeatsBooster     = type.childSeatsBooster,
            comment               = type.comment,
            waterTaxi             = type.waterTaxi,
            price                 = type.price,
            vehicle               = vehicleInfoMapper.toView(type.vehicle),
            timeToTransfer        = tr,
            tripStatus            = getTripStatus(tr, type.time, type.duration?.times(60))
        )
    }

    override fun fromView(type: CarrierTripBaseModel): CarrierTripBase { throw UnsupportedOperationException() }

    companion object {
        private fun getTripStatus(timeToTransfer: Int, time: Int?, duration: Int?) = when {
            timeToTransfer > 0                         -> CarrierTripBaseModel.FUTURE_TRIP
            timeToTransfer + (time?: duration?: 0) > 0 -> CarrierTripBaseModel.IN_PROGRESS_TRIP
            else                                       -> CarrierTripBaseModel.PAST_TRIP
        }
    }
}
