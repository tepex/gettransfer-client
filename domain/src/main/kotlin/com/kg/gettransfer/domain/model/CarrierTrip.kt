package com.kg.gettransfer.domain.model

import java.util.Date

data class CarrierTrip(
    val base: CarrierTripBase,
    val pax: Int?, /*may be null only from cache*/
    val nameSign: String?,
    val flightNumber: String?,
    val paidSum: String?, /*may be null only from cache*/
    val remainsToPay: String?, /*may be null only from cache*/
    val paidPercentage: Int?, /*may be null only from cache*/
    val passengerAccount: PassengerAccount? /*may be null only from cache*/
)

data class PassengerAccount(
    override val id: Long,
    val profile: Profile,
    val lastSeen: Date
) : Entity()
