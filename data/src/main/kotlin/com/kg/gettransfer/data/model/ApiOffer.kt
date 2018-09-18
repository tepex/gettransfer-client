package com.kg.gettransfer.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ApiOffers(@SerializedName("offers") @Expose var offers: List<ApiOffer>)

class ApiOffer(@SerializedName("id") @Expose var id: Long,
               @SerializedName("status") @Expose var status: String,
               @SerializedName("wifi") @Expose var wifi: Boolean,
               @SerializedName("refreshments") @Expose var refreshments: Boolean,
               @SerializedName("created_at") @Expose var createdAt: String,
               @SerializedName("price") @Expose var price: ApiPrice,
               @SerializedName("ratings") @Expose var ratings: ApiRatings?,
               @SerializedName("passenger_feedback") @Expose var passengerFeedback: String?,
               @SerializedName("carrier") @Expose var carrier: ApiCarrier,
               @SerializedName("vehicle") @Expose var vehicle: ApiVehicle,
               @SerializedName("driver") @Expose var driver: ApiDriver?)

class ApiPrice(@SerializedName("base") @Expose var base: ApiMoney,
               @SerializedName("percentage_30") @Expose var percentage30: String,
               @SerializedName("percentage_70") @Expose var percentage70: String,
               @SerializedName("amount") @Expose var amount: Double)

class ApiRatings(@SerializedName("average") @Expose var average: Double?,
                 @SerializedName("vehicle") @Expose var vehicle: Double?,
                 @SerializedName("driver") @Expose var driver: Double?,
                 @SerializedName("fair") @Expose var fair: Double?)

class ApiCarrier(@SerializedName("title") @Expose var title: String?,
                 @SerializedName("email") @Expose var email: String?,
                 @SerializedName("phone") @Expose var phone: String?,
                 @SerializedName("id") @Expose var id: Long,
                 @SerializedName("approved") @Expose var approved: Boolean,
                 @SerializedName("completed_transfers") @Expose var completedTransfers: Int,
                 @SerializedName("languages") @Expose var languages: List<ApiLocales>,
                 @SerializedName("ratings") @Expose var ratings: ApiRatings,
                 @SerializedName("can_update_offers") @Expose var canUpdateOffers: Boolean?)

class ApiVehicle(@SerializedName("name") @Expose var name: String,
                 @SerializedName("registration_number") @Expose var registrationNumber: String,
                 @SerializedName("year") @Expose var year: Int,
                 @SerializedName("color") @Expose var color: String,
                 @SerializedName("transport_type_id") @Expose var transportTypeId: String,
                 @SerializedName("pax_max") @Expose var paxMax: Int,
                 @SerializedName("luggage_max") @Expose var luggageMax: Int,
                 @SerializedName("photos") @Expose var photos: List<String>)

class ApiDriver(@SerializedName("full_name") @Expose var fullName: String,
                @SerializedName("phone") @Expose var phone: String,
                @SerializedName("email") @Expose var email: String)
