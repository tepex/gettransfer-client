package com.kg.gettransfer.presentation.model

class CarrierTripModel(val transferId: Long,
                       val from: String,
                       val to: String,
                       val dateTime: String,
                       val distance: String,
                       val countPassengers: Int,
                       val passengerName: String,
                       val countChild: Int,
                       val flightNumber: String?,
                       val comment: String?,
                       val pay: String?)