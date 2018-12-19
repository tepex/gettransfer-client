package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.CarrierTripBase

import com.kg.gettransfer.presentation.model.CarrierTripBaseModel

import com.kg.gettransfer.presentation.ui.SystemUtils

import org.koin.standalone.get

open class CarrierTripBaseMapper : Mapper<CarrierTripBaseModel, CarrierTripBase> {
    private val vehicleInfoMapper = get<VehicleInfoMapper>()

    override fun toView(type: CarrierTripBase) =
        CarrierTripBaseModel(
            id                    = type.id,
            transferId            = type.transferId,
            from                  = type.from.name!!,
            to                    = type.to?.name,
            dateTime              = SystemUtils.formatDateTime(type.dateLocal),
            duration              = type.duration,
            distance              = type.distance ?: Mapper.checkDistance(type.from.point!!, type.to!!.point!!),
            time                  = type.time,
            countChild            = type.childSeats,
            childSeatsInfant      = type.childSeatsInfant,
            childSeatsConvertible = type.childSeatsConvertible,
            childSeatsBooster     = type.childSeatsBooster,
            comment               = type.comment,
            waterTaxi             = type.waterTaxi,
            pay                   = type.price,
            vehicle               = vehicleInfoMapper.toView(type.vehicle)
        )

    override fun fromView(type: CarrierTripBaseModel): CarrierTripBase { throw UnsupportedOperationException() }
}
