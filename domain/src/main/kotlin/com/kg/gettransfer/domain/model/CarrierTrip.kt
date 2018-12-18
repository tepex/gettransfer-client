package com.kg.gettransfer.domain.model

import java.util.Date

data class CarrierTrip(
    override val id: Long,
    override val transferId: Long,
    override val from: CityPoint,
    override val to: CityPoint?,
    override val dateLocal: Date,
    override val duration: Int?,
    override val distance: Int?,
    override val time: Int?,
    override val childSeats: Int,
    override val childSeatsInfant: Int,
    override val childSeatsConvertible: Int,
    override val childSeatsBooster: Int,
    override val comment: String?,
    override val waterTaxi: Boolean,
    override val price: String,
    override val vehicle: VehicleInfo,
    val pax: Int?,
    val nameSign: String?,
    val flightNumber: String?,
    val paidSum: String?,
    val remainToPay: String?,
    val paidPercentage: Int?,
    val passengerAccount: PassengerAccount?
) : CarrierTripBase(
    id,
    transferId,
    from,
    to,
    dateLocal,
    duration,
    distance,
    time,
    childSeats,
    childSeatsInfant,
    childSeatsConvertible,
    childSeatsBooster,
    comment,
    waterTaxi,
    price,
    vehicle
)

data class PassengerAccount(
    override val id: Long,
    val profile: Profile,
    val lastSeen: Date
) : Entity()
