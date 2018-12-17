package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kg.gettransfer.data.model.CarrierTripEntity
import com.kg.gettransfer.data.model.PassengerAccountEntity

class CarrierTripModelWrapper(@SerializedName(CarrierTripEntity.ENTITY_NAME) @Expose val trip: CarrierTripModel)

class CarrierTripsModel(@SerializedName("trips") @Expose val trips: List<CarrierTripModel>)

class CarrierTripModel(
    @SerializedName(CarrierTripEntity.ID) @Expose val id: Long,
    @SerializedName(CarrierTripEntity.TRANSFER_ID) @Expose val transferId: Long,
    @SerializedName(CarrierTripEntity.FROM) @Expose val from: CityPointModel,
    @SerializedName(CarrierTripEntity.TO) @Expose val to: CityPointModel,
    @SerializedName(CarrierTripEntity.DATE_LOCAL) @Expose val dateLocal: String,
    @SerializedName(CarrierTripEntity.DURATION) @Expose val duration: Int?,
    @SerializedName(CarrierTripEntity.DISTANCE) @Expose val distance: Int,
    @SerializedName(CarrierTripEntity.TIME) @Expose val time: Int,
    @SerializedName(CarrierTripEntity.CHILD_SEATS) @Expose val childSeats: Int,
    @SerializedName(CarrierTripEntity.COMMENT) @Expose val comment: String?,
    @SerializedName(CarrierTripEntity.WATER_TAXI) @Expose val waterTaxi: Boolean,
    @SerializedName(CarrierTripEntity.PRICE) @Expose val price: String,
    @SerializedName(CarrierTripEntity.VEHICLE) @Expose val vehicle: VehicleInfoModel,
    @SerializedName(CarrierTripEntity.PAX) @Expose val pax: Int?,
    @SerializedName(CarrierTripEntity.NAME_SIGN) @Expose val nameSign: String?,
    @SerializedName(CarrierTripEntity.FLIGHT_NUMBER) @Expose val flightNumber: String?,
    @SerializedName(CarrierTripEntity.PAID_SUM) @Expose val paidSum: String?,
    @SerializedName(CarrierTripEntity.REMAINS_TO_PAY) @Expose val remainToPay: String?,
    @SerializedName(CarrierTripEntity.PAID_PERCENTAGE) @Expose val paidPercentage: Int?,
    @SerializedName(CarrierTripEntity.PASSENGER_ACCOUNT) @Expose val passengerAccount: PassengerAccountModel?
)

class PassengerAccountModel(
    fullName: String,
    email: String,
    phone: String,
    @SerializedName(PassengerAccountEntity.LAST_SEEN) @Expose val lastSeen: String
) : ProfileModel(fullName, email, phone)
