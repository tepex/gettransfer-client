package com.kg.gettransfer.domain.model

import java.util.Date

data class CarrierTripBase(
    override val id: Long,
    val transferId: Long,
    val from: CityPoint,
    val to: CityPoint?,
    val dateLocal: Date,
    val duration: Int?,
    val distance: Int?,
    val time: Int?,
    val childSeats: Int,
    val childSeatsInfant: Int,
    val childSeatsConvertible: Int,
    val childSeatsBooster: Int,
    val comment: String?,
    val waterTaxi: Boolean,
    val price: String,
    val vehicle: VehicleInfo
) : Entity() {
    companion object {
        val EMPTY = CarrierTripBase(
            id                    = 0,
            transferId            = 0,
            from                  = CityPoint.EMPTY,
            to                    = CityPoint.EMPTY,
            dateLocal             = Date(),
            duration              = null,
            distance              = null,
            time                  = 0,
            childSeats            = 0,
            childSeatsInfant      = 0,
            childSeatsConvertible = 0,
            childSeatsBooster     = 0,
            comment               = null,
            waterTaxi             = false,
            price                 = "",
            vehicle               = VehicleInfo("", "")
        )
    }
}
