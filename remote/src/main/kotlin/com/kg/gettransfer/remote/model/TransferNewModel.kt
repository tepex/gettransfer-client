package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TransferNewWrapperModel(@SerializedName("transfer") @Expose val transfer: TransferNewModel)

class TransferNewModel(@SerializedName("from") @Expose val from: CityPointModel,
                       @SerializedName("to") @Expose val to: CityPointModel,
                       @SerializedName("trip_to") @Expose val tripTo: TripModel,
                       @SerializedName("trip_return") @Expose val tripReturn: TripModel?,
                       @SerializedName("transport_type_ids") @Expose val transportTypeIds: List<String>,
                       @SerializedName("pax") @Expose val pax: Int,
                       @SerializedName("child_seats") @Expose val childSeats: Int?,
                       @SerializedName("passenger_offered_price") @Expose val passengerOfferedPrice: Double?,
                       @SerializedName("name_sign") @Expose val nameSign: String?,
                       @SerializedName("comment") @Expose val comment: String?,
                       @SerializedName("passenger_account") @Expose val user: UserModel,
                       @SerializedName("promo_code") @Expose val promoCode: String?)
/* Not used now
                         @SerializedName("paypal_only") @Expose val paypalOnly: Boolean) */

class TripModel(@SerializedName("date") @Expose val date: String,
                @SerializedName("time") @Expose val time: String,
                @SerializedName("flight_number") @Expose val flight: String?)
