package com.kg.gettransfer.domain.model

import java.util.Date

open class CarrierTripBase(
    override val id: Long,
    open val transferId: Long,
    open val from: CityPoint,
    open val to: CityPoint?,
    open val dateLocal: Date,
    open val duration: Int?,
    open val distance: Int?,
    open val time: Int?,
    open val childSeats: Int,
    open val childSeatsInfant: Int,
    open val childSeatsConvertible: Int,
    open val childSeatsBooster: Int,
    open val comment: String?,
    open val waterTaxi: Boolean,
    open val price: String,
    open val vehicle: VehicleInfo
) : Entity()
