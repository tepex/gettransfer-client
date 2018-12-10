package com.kg.gettransfer.presentation.model

class CarrierTripModel(
    val tripId: Long,
    val transferId: Long,
    val from: String,
    val to: String,
    val dateTime: String,
    val distance: Int?,
    val countChild: Int,
    val comment: String?,
    val pay: String?,
    val vehicleName: String,
    val countPassengers: Int?,
    val nameSign: String?,
    val flightNumber: String?,
    val remainsToPay: String?
)
