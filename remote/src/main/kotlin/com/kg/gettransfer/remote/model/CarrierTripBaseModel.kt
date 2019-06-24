package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.model.CarrierTripBaseEntity

data class CarrierTripsBaseModel(@SerializedName("trips") @Expose val trips: List<CarrierTripBaseModel>)

open class CarrierTripBaseModel(
    @SerializedName(CarrierTripBaseEntity.ID) @Expose val id: Long,
    @SerializedName(CarrierTripBaseEntity.TRANSFER_ID) @Expose val transferId: Long,
    @SerializedName(CarrierTripBaseEntity.FROM) @Expose val from: CityPointModel,
    @SerializedName(CarrierTripBaseEntity.TO) @Expose val to: CityPointModel?,
    @SerializedName(CarrierTripBaseEntity.DATE_LOCAL) @Expose val dateLocal: String,
    @SerializedName(CarrierTripBaseEntity.DURATION) @Expose val duration: Int?,
    @SerializedName(CarrierTripBaseEntity.DISTANCE) @Expose val distance: Int?,
    @SerializedName(CarrierTripBaseEntity.TIME) @Expose val time: Int?,
    @SerializedName(CarrierTripBaseEntity.CHILD_SEATS) @Expose val childSeats: Int,
    @SerializedName(CarrierTripBaseEntity.CHILD_SEATS_INFANT) @Expose val childSeatsInfant: Int,
    @SerializedName(CarrierTripBaseEntity.CHILD_SEATS_CONVERTIBLE) @Expose val childSeatsConvertible: Int,
    @SerializedName(CarrierTripBaseEntity.CHILD_SEATS_BOOSTER) @Expose val childSeatsBooster: Int,
    @SerializedName(CarrierTripBaseEntity.COMMENT) @Expose val comment: String?,
    @SerializedName(CarrierTripBaseEntity.WATER_TAXI) @Expose val waterTaxi: Boolean,
    @SerializedName(CarrierTripBaseEntity.PRICE) @Expose val price: String,
    @SerializedName(CarrierTripBaseEntity.VEHICLE) @Expose val vehicle: VehicleInfoModel
)

fun CarrierTripBaseModel.map() =
    CarrierTripBaseEntity(
        id,
        transferId,
        from.map(),
        to?.map(),
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
        vehicle.map()
    )
