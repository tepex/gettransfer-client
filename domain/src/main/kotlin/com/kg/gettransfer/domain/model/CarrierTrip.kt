package com.kg.gettransfer.domain.model

import java.util.Date

data class CarrierTrip(
    val base: CarrierTripBase,
    val pax: Int,
    val nameSign: String?,
    val flightNumber: String?,
    val paidSum: String,
    val remainsToPay: String,
    val paidPercentage: Int,
    val passengerAccount: PassengerAccount
)

data class PassengerAccount(
    override val id: Long,
    val profile: Profile,
    val lastSeen: Date
) : Entity()
