package com.kg.gettransfer.presentation.model

import java.util.Date

data class CarrierTripBaseModel(
    val id: Long,
    val transferId: Long,
    val from: String,
    val to: String?,
    val dateTime: String,
    val duration: Int?,
    val distance: Int,
    val time: Int?,
    val countChild: Int,
    val childSeatsInfant: Int,
    val childSeatsConvertible: Int,
    val childSeatsBooster: Int,
    val comment: String?,
    val waterTaxi: Boolean,
    val pay: String,
    val vehicle: VehicleInfoModel
)
