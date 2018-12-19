package com.kg.gettransfer.presentation.model

data class CarrierTripModel(
    val base: CarrierTripBaseModel,
    val countPassengers: Int,
    val nameSign: String?,
    val flightNumber: String?,
    val paidSum: String,
    val remainsToPay: String,
    val paidPercentage: Int,
    val passenger: PassengerAccountModel
)
