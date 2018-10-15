package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CarrierTripModelWrapper(@SerializedName("trip") @Expose var trip: CarrierTripModel)

class CarrierTripsModel(@SerializedName("trips") @Expose var trips: List<CarrierTripModel>)

class CarrierTripModel(@SerializedName("id") @Expose var id: Long,
                       @SerializedName("transfer_id") @Expose var transferId: Long,
                       @SerializedName("from") @Expose var from: CityPointModel,
                       @SerializedName("to") @Expose var to: CityPointModel,
                       @SerializedName("date_local") @Expose var dateLocal: String,
                       @SerializedName("duration") @Expose var duration: Int?,
                       @SerializedName("distance") @Expose var distance: Int,
                       @SerializedName("time") @Expose var time: Int,
                       @SerializedName("child_seats") @Expose var childSeats: Int,
                       @SerializedName("comment") @Expose var comment: String?,
                       @SerializedName("water_taxi") @Expose var waterTaxi: Boolean,
                       @SerializedName("price") @Expose var price: String,
                       @SerializedName("vehicle") @Expose var vehicle: VehicleBaseModel,
                       @SerializedName("pax") @Expose var pax: Int?,
                       @SerializedName("name_sign") @Expose var nameSign: String?,
                       @SerializedName("flight_number") @Expose var flightNumber: String?,
                       @SerializedName("paid_sum") @Expose var paidSum: String?,
                       @SerializedName("remains_to_pay") @Expose var remainToPay: String?,
                       @SerializedName("paid_percentage") @Expose var paidPercentage: Int?,
                       @SerializedName("passenger_account") @Expose var passengerAccount: PassengerAccountModel?)

class PassengerAccountModel(@SerializedName("email") @Expose var email: String,
                            @SerializedName("phone") @Expose var phone: String,
                            @SerializedName("full_name") @Expose var fullName: String,
                            @SerializedName("last_seen") @Expose var lastSeen: String)
