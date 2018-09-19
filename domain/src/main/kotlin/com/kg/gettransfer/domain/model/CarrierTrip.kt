package com.kg.gettransfer.domain.model

import java.util.Date

data class CarrierTrip(val id: Long,
                       val transferId: Long,
                       val from: CityPoint,
                       val to: CityPoint,
                       val dateLocal: Date,
                       val duration: Int?,
                       val distance: Int,
                       val time: Int,
                       val childSeats: Int,
                       val comment: String?,
                       val waterTaxi: Boolean,
                       val price: String,
                       val vehicle: CarrierTripVehicle,
                       val pax: Int?,
                       val nameSign: String?,
                       val flightNumber: String?,
                       val paidSum: String?,
                       val remainToPay: String?,
                       val paidPercentage: Int?,
                       val passengerAccount: PassengerAccount?)

data class PassengerAccount(val email: String,
                            val phone: String,
                            val fullName: String,
                            val lastSeen: String)

data class CarrierTripVehicle(val name: String,
                              val registrationNumber: String)