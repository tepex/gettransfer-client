package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CarrierTripModelWrapper(@SerializedName("trip") @Expose val trip: CarrierTripModel)

class CarrierTripsModel(@SerializedName("trips") @Expose val trips: List<CarrierTripModel>)

class CarrierTripModel(@SerializedName("id") @Expose val id: Long,
                       @SerializedName("transfer_id") @Expose val transferId: Long,
                       @SerializedName("from") @Expose val from: CityPointModel,
                       @SerializedName("to") @Expose val to: CityPointModel,
                       @SerializedName("date_local") @Expose val dateLocal: String,
                       @SerializedName("hourlyDuration") @Expose val duration: Int?,
                       @SerializedName("distance") @Expose val distance: Int,
                       @SerializedName("time") @Expose val time: Int,
                       @SerializedName("child_seats") @Expose val childSeats: Int,
                       @SerializedName("comment") @Expose val comment: String?,
                       @SerializedName("water_taxi") @Expose val waterTaxi: Boolean,
                       @SerializedName("price") @Expose val price: String,
                       @SerializedName("vehicle") @Expose val vehicle: VehicleBaseModel,
                       @SerializedName("pax") @Expose val pax: Int?,
                       @SerializedName("name_sign") @Expose val nameSign: String?,
                       @SerializedName("flight_number") @Expose val flightNumber: String?,
                       @SerializedName("paid_sum") @Expose val paidSum: String?,
                       @SerializedName("remains_to_pay") @Expose val remainToPay: String?,
                       @SerializedName("paid_percentage") @Expose val paidPercentage: Int?,
                       @SerializedName("passenger_account") @Expose val passengerAccount: PassengerAccountModel?)

class PassengerAccountModel(fullName: String, email: String, phone: String,
                            @SerializedName("last_seen") @Expose val lastSeen: String): ProfileModel(fullName, email, phone)
