package com.kg.gettransfer.data.model

data class CarrierTripEntity(
    val id: Long,
    val transferId: Long,
    val from: CityPointEntity,
    var to: CityPointEntity,
    val dateLocal: String,
    val duration: Int?,
    val distance: Int?,
    val time: Int?,
    val childSeats: Int,
    val comment: String?,
    var waterTaxi: Boolean,
    val price: String, /* formatted, i.e "$10.00" */
    val vehicleBase: VehicleBaseEntity,
    val pax: Int?, /* passengers count */
    val nameSign: String?,
    val flightNumber: String?,
    val paidSum: String?, /* formatted */
    val remainToPay: String?, /* formatted */
    val paidPercentage: Int?,
    val passengerAccount: PassengerAccountEntity?
)

data class PassengerAccountEntity(val profile: ProfileEntity, var lastSeen: String)
