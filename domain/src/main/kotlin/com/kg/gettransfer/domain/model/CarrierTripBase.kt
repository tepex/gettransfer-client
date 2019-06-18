package com.kg.gettransfer.domain.model

import java.util.Date

data class CarrierTripBase(
    override val id: Long,
    val transferId: Long,
    val from: CityPoint,
    val to: CityPoint,
    val dateLocal: Date,
    val duration: Int,
    val distance: Int,
    val time: Int,
    val childSeats: Int,
    val childSeatsInfant: Int,
    val childSeatsConvertible: Int,
    val childSeatsBooster: Int,
    val comment: String,
    val waterTaxi: Boolean,
    val price: String,
    val vehicle: VehicleInfo
) : Entity() {

    companion object {
        const val NO_ID = -1L
        const val NO_DURATION = -1
        const val NO_DISTANCE = -1
        const val NO_TIME = -1
        const val NO_COMMENT = ""

        val EMPTY = CarrierTripBase(
            id                    = NO_ID,
            transferId            = NO_ID,
            from                  = CityPoint.EMPTY,
            to                    = CityPoint.EMPTY,
            dateLocal             = Date(),
            duration              = NO_DURATION,
            distance              = NO_DISTANCE,
            time                  = NO_TIME,
            childSeats            = 0,
            childSeatsInfant      = 0,
            childSeatsConvertible = 0,
            childSeatsBooster     = 0,
            comment               = NO_COMMENT,
            waterTaxi             = false,
            price                 = "",
            vehicle               = VehicleInfo.EMPTY
        )
    }
}
