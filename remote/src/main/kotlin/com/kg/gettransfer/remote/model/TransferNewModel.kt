package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TransferNewWrapperModel(@SerializedName("transfer") @Expose val transfer: TransferNewBase)

open class TransferNewBase(@SerializedName("from") @Expose val from: CityPointModel,
                           @SerializedName("to") @Expose var to: CityPointModel? = null,
                           @SerializedName("trip_to") @Expose val tripTo: TripModel,
                           @SerializedName("transport_type_ids") @Expose val transportTypeIds: List<String>,
                           @SerializedName("pax") @Expose val pax: Int,
                           @SerializedName("child_seats") @Expose val childSeats: Int?,
                           @SerializedName("passenger_offered_price") @Expose val passengerOfferedPrice: Double?,
                           @SerializedName("name_sign") @Expose val nameSign: String?,
                           @SerializedName("comment") @Expose val comment: String?,
                           @SerializedName("passenger_account") @Expose val user: UserModel,
                           @SerializedName("promo_code") @Expose val promoCode: String?)


class TransferPointToPointNewModel(from: CityPointModel,
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
                       @SerializedName("trip_return") @Expose var tripReturn: TripModel? = null):
                       TransferNewBase(from, to, tripTo, transportTypeIds, pax, childSeats, passengerOfferedPrice, nameSign, comment, user, promoCode)
/* Not used now
                         @SerializedName("paypal_only") @Expose val paypalOnly: Boolean) */

class TransferHourlyNewModel(from: CityPointModel,
                             tripTo: TripModel,
                             transportTypeIds: List<String>,
                             pax: Int,
                             childSeats: Int?,
                             passengerOfferedPrice: Double?,
                             nameSign: String?,
                             comment: String?,
                             user: UserModel,
                             promoCode: String?,
                             @SerializedName("duration") @Expose var duration: Int ):
                             TransferNewBase(from, null, tripTo, transportTypeIds, pax, childSeats, passengerOfferedPrice, nameSign, comment, user, promoCode)

class TripModel(@SerializedName("date") @Expose val date: String,
                @SerializedName("time") @Expose val time: String,
                @SerializedName("flight_number") @Expose val flight: String?)
