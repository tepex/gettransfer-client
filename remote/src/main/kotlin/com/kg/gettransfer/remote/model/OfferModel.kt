package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OffersModel(@SerializedName("offers") @Expose val offers: List<OfferModel>)

class OfferModel(@SerializedName("id") @Expose val id: Long,
                 @SerializedName("status") @Expose val status: String,
                 @SerializedName("wifi") @Expose val wifi: Boolean,
                 @SerializedName("refreshments") @Expose val refreshments: Boolean,
                 @SerializedName("created_at") @Expose val createdAt: String,
                 @SerializedName("updated_at") @Expose val updatedAt: String?,
                 @SerializedName("price") @Expose val price: PriceModel,
                 @SerializedName("ratings") @Expose val ratings: RatingsModel?,
                 @SerializedName("passenger_feedback") @Expose val passengerFeedback: String?,
                 @SerializedName("carrier") @Expose val carrier: CarrierModel,
                 @SerializedName("vehicle") @Expose val vehicle: VehicleModel,
                 @SerializedName("driver") @Expose val driver: ProfileModel?)

class PriceModel(@SerializedName("base") @Expose val base: MoneyModel,
                 @SerializedName("percentage_30") @Expose val percentage30: String,
                 @SerializedName("percentage_70") @Expose val percentage70: String,
                 @SerializedName("amount") @Expose val amount: Double)

class RatingsModel(@SerializedName("average") @Expose val average: Float?,
                   @SerializedName("vehicle") @Expose val vehicle: Float?,
                   @SerializedName("driver") @Expose val driver: Float?,
                   @SerializedName("fair") @Expose val fair: Float?)

class CarrierModel(@SerializedName("title") @Expose val title: String?,
                   @SerializedName("email") @Expose val email: String?,
                   @SerializedName("phone") @Expose val phone: String?,
                   @SerializedName("id") @Expose val id: Long,
                   @SerializedName("approved") @Expose val approved: Boolean,
                   @SerializedName("completed_transfers") @Expose val completedTransfers: Int,
                   @SerializedName("languages") @Expose val languages: List<LocaleModel>,
                   @SerializedName("ratings") @Expose val ratings: RatingsModel,
                   @SerializedName("can_update_offers") @Expose val canUpdateOffers: Boolean?)

class VehicleModel(name: String, regNumber: String,
                   @SerializedName("year") @Expose val year: Int,
                   @SerializedName("color") @Expose val color: String?,
                   @SerializedName("transport_type_id") @Expose val transportTypeId: String,
                   @SerializedName("pax_max") @Expose val paxMax: Int,
                   @SerializedName("luggage_max") @Expose val luggageMax: Int,
                   @SerializedName("photos") @Expose var photos: List<String>): VehicleBaseModel(name, regNumber)
