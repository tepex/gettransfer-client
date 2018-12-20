package com.kg.gettransfer.presentation.model

import java.util.Date

data class CarrierTripBaseModel(
    val id: Long,
    val transferId: Long,
    val from: String,
    val to: String?,
    val dateTime: String,
    val duration: Int?,
    val distance: Int?,
    val time: Int?,
    val countChild: Int,
    val childSeatsInfant: Int,
    val childSeatsConvertible: Int,
    val childSeatsBooster: Int,
    val comment: String?,
    val waterTaxi: Boolean,
    val price: String,
    val vehicle: VehicleInfoModel,
    val timeToTransfer: Int,
    val tripStatus: String
) {

    companion object {
        const val FUTURE_TRIP      = "future_trip"
        const val IN_PROGRESS_TRIP = "now_trip"
        const val PAST_TRIP        = "past_trip"
    }
}
