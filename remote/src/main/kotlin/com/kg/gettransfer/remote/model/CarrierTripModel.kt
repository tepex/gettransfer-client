package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.model.CarrierTripEntity
import com.kg.gettransfer.data.model.PassengerAccountEntity
import com.kg.gettransfer.data.model.ProfileEntity

data class CarrierTripModelWrapper(@SerializedName("trip") @Expose val trip: CarrierTripModel)

class CarrierTripModel(
    id: Long,
    transferId: Long,
    from: CityPointModel,
    to: CityPointModel,
    dateLocal: String,
    duration: Int?,
    distance: Int,
    time: Int,
    childSeats: Int,
    childSeatsInfant: Int,
    childSeatsConvertible: Int,
    childSeatsBooster: Int,
    comment: String?,
    waterTaxi: Boolean,
    price: String,
    vehicle: VehicleInfoModel,
    @SerializedName(CarrierTripEntity.PAX) @Expose val pax: Int,
    @SerializedName(CarrierTripEntity.NAME_SIGN) @Expose val nameSign: String?,
    @SerializedName(CarrierTripEntity.FLIGHT_NUMBER) @Expose val flightNumber: String?,
    @SerializedName(CarrierTripEntity.PAID_SUM) @Expose val paidSum: String,
    @SerializedName(CarrierTripEntity.REMAINS_TO_PAY) @Expose val remainsToPay: String,
    @SerializedName(CarrierTripEntity.PAID_PERCENTAGE) @Expose val paidPercentage: Int,
    @SerializedName(CarrierTripEntity.PASSENGER_ACCOUNT) @Expose val passengerAccount: PassengerAccountModel
) : CarrierTripBaseModel(
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

class PassengerAccountModel(
    fullName: String?,
    email: String,
    phone: String,
    @SerializedName(PassengerAccountEntity.ID) @Expose val id: Long,
    @SerializedName(PassengerAccountEntity.LAST_SEEN) @Expose val lastSeen: String?
) : ProfileModel(fullName, email, phone)

fun PassengerAccountModel.map() = PassengerAccountEntity(id, ProfileEntity(fullName, email, phone), lastSeen)

fun CarrierTripModel.map() =
    CarrierTripEntity(
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
        vehicle.map(),
        pax,
        nameSign,
        flightNumber,
        paidSum,
        remainsToPay,
        paidPercentage,
        passengerAccount.map()
    )
