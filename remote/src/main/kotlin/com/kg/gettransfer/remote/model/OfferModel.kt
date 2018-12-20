package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kg.gettransfer.data.model.OfferEntity

data class OffersModel(@SerializedName("offers") @Expose val offers: List<OfferModel>)

data class OfferModel(
    @SerializedName(OfferEntity.ID) @Expose val id: Long,
    @SerializedName(OfferEntity.STATUS) @Expose val status: String,
    @SerializedName(OfferEntity.WIFI) @Expose val wifi: Boolean,
    @SerializedName(OfferEntity.REFRESHMENTS) @Expose val refreshments: Boolean,
    @SerializedName(OfferEntity.CREATED_AT) @Expose val createdAt: String,
    @SerializedName(OfferEntity.UPDATED_AT) @Expose val updatedAt: String?,
    @SerializedName(OfferEntity.PRICE) @Expose val price: PriceModel,
    @SerializedName(OfferEntity.RATINGS) @Expose val ratings: RatingsModel?,
    @SerializedName(OfferEntity.PASSENGER_FEEDBACK) @Expose val passengerFeedback: String?,
    @SerializedName(OfferEntity.CARRIER) @Expose val carrier: CarrierModel,
    @SerializedName(OfferEntity.VEHICLE) @Expose val vehicle: VehicleModel,
    @SerializedName(OfferEntity.DRIVER) @Expose val driver: ProfileModel?
)
