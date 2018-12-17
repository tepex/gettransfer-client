package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kg.gettransfer.data.model.TransferNewEntity
import com.kg.gettransfer.data.model.TripEntity

class TransferNewWrapperModel(@SerializedName(TransferNewEntity.ENTITY_NAME) @Expose val transfer: TransferNewBase)

open class TransferNewBase(
    @SerializedName(TransferNewEntity.FROM) @Expose val from: CityPointModel,
    @SerializedName(TransferNewEntity.TO) @Expose var to: CityPointModel? = null,
    @SerializedName(TransferNewEntity.TRIP_TO) @Expose val tripTo: TripModel,
    @SerializedName(TransferNewEntity.TRANSPORT_TYPE_IDS) @Expose val transportTypeIds: List<String>,
    @SerializedName(TransferNewEntity.PAX) @Expose val pax: Int,
    @SerializedName(TransferNewEntity.CHILD_SEATS) @Expose val childSeats: Int?,
    @SerializedName(TransferNewEntity.PASSENGER_OFFERED_PRICE) @Expose val passengerOfferedPrice: Double?,
    @SerializedName(TransferNewEntity.NAME_SIGN) @Expose val nameSign: String?,
    @SerializedName(TransferNewEntity.COMMENT) @Expose val comment: String?,
    @SerializedName(TransferNewEntity.PASSENGER_ACCOUNT) @Expose val user: UserModel,
    @SerializedName(TransferNewEntity.PROMO_CODE) @Expose val promoCode: String?
)

class TransferPointToPointNewModel(
    from: CityPointModel,
    to: CityPointModel,
    tripTo: TripModel,
    transportTypeIds: List<String>,
    pax: Int,
    childSeats: Int?,
    passengerOfferedPrice: Double?,
    nameSign: String?,
    comment: String?,
    user: UserModel,
    promoCode: String?,
/* Not used now
    @SerializedName("paypal_only") @Expose val paypalOnly: Boolean */
    @SerializedName(TransferNewEntity.TRIP_RETURN) @Expose var tripReturn: TripModel? = null
) : TransferNewBase(from, to, tripTo, transportTypeIds, pax, childSeats, passengerOfferedPrice, nameSign, comment, user, promoCode)

class TransferHourlyNewModel(
    from: CityPointModel,
    tripTo: TripModel,
    transportTypeIds: List<String>,
    pax: Int,
    childSeats: Int?,
    passengerOfferedPrice: Double?,
    nameSign: String?,
    comment: String?,
    user: UserModel,
    promoCode: String?,
    @SerializedName(TransferNewEntity.DURATION) @Expose var duration: Int
) : TransferNewBase(from, null, tripTo, transportTypeIds, pax, childSeats, passengerOfferedPrice, nameSign, comment, user, promoCode)

data class TripModel(
    @SerializedName(TripEntity.DATE) @Expose val date: String,
    @SerializedName(TripEntity.TIME) @Expose val time: String,
    @SerializedName(TripEntity.FLIGHT_NUMBER) @Expose val flight: String?
)
