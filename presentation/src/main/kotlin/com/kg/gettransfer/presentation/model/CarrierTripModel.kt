package com.kg.gettransfer.presentation.model

data class CarrierTripModel(
    val base: CarrierTripBaseModel,
    val countPassengers: Int?, /*may be null only from cache*/
    val nameSign: String?,
    val flightNumber: String?,
    val paidSum: String?, /*may be null only from cache*/
    val totalPrice: TotalPriceModel?, /*may be null only from cache*/
    val passenger: PassengerAccountModel? /*may be null only from cache*/
)

data class TotalPriceModel(
    val remainsToPay: String,
    val paidPercentage: Int
)
