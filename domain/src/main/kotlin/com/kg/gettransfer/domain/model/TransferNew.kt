package com.kg.gettransfer.domain.model

import java.util.Date

data class TransferNew(
    val from: CityPoint,
    val dest: Dest<CityPoint, Int>,
    val tripTo: Trip,
    val tripReturn: Trip?,
    val transportTypeIds: List<TransportType.ID>,
    val pax: Int,
    val childSeats: Int?,
    val passengerOfferedPrice: Int?, // x 100 (in cents)
    val comment: String?,
    val user: User,
    val promoCode: String = "",
    val paypalOnly: Boolean
)

data class Trip(
    val dateTime: Date,
    val flightNumber: String?
)
