package com.kg.gettransfer.data.model

import java.util.Date

data class CarrierTripEntity(val id: Long,
                             val transferId: Long,
                             val from: CityPointEntity,
                             var to: CityPointEntity,
                             val dateLocal: Date,
                             val duration: Int?,
                             val distance: Int,
                             val time: Int,
                             val childSeats: Int,
                             val comment: String?,
                             var waterTaxi: Boolean,
                             val price: String, /* formatted, i.e "$10.00" */
                             val vehicle: CarrierTripVehicleEntity,
                             val pax: Int?, /* passengers count */
                             val nameSign: String?,
                             val flightNumber: String?,
                             val paidSum: String?, /* formatted */
                             val remainToPay: String?, /* formatted */
                             val paidPercentage: Int?,
                             val passengerAccount: PassengerAccountEntity?)

data class PassengerAccountEntity(val user: UserEntity, var lastSeen: Date)

data class CarrierTripVehicleEntity(val name: String, val registrationNumber: String)
