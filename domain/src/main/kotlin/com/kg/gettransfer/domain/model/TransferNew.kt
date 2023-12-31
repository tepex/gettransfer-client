package com.kg.gettransfer.domain.model

import com.kg.gettransfer.core.domain.CityPoint

import java.util.Date

data class TransferNew(
    val from: CityPoint,
    val dest: Dest<CityPoint, Int>,
    val tripTo: Trip,
    val tripReturn: Trip?,
    val transportTypeIds: List<TransportType.ID>,
    val pax: Int,
    val childSeatsInfant: Int?,
    val childSeatsConvertible: Int?,
    val childSeatsBooster: Int?,
    val passengerOfferedPrice: Int?, // x 100 (in cents)
    val comment: String?,
    val nameSign: String? = null,
    val user: User,
    val promoCode: String = "",
    val paypalOnly: Boolean
)

data class Trip(
    val dateTime: Date,
    val flightNumber: String?
)
