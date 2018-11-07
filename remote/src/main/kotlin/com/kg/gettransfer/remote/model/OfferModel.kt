package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kg.gettransfer.data.model.CarrierEntity
import com.kg.gettransfer.data.model.OfferEntity
import com.kg.gettransfer.data.model.PriceEntity
import com.kg.gettransfer.data.model.RatingsEntity
import com.kg.gettransfer.data.model.VehicleEntity

class OffersModel(@SerializedName("offers") @Expose val offers: List<OfferModel>)

class OfferModel(@SerializedName(OfferEntity.ID) @Expose val id: Long,
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
                 @SerializedName(OfferEntity.DRIVER) @Expose val driver: ProfileModel?)

class PriceModel(@SerializedName(PriceEntity.BASE) @Expose val base: MoneyModel,
                 @SerializedName(PriceEntity.PERCENTAGE_30) @Expose val percentage30: String,
                 @SerializedName(PriceEntity.PERCENTAGE_70) @Expose val percentage70: String,
                 @SerializedName(PriceEntity.AMOUNT) @Expose val amount: Double)

class RatingsModel(@SerializedName(RatingsEntity.AVERAGE) @Expose val average: Float?,
                   @SerializedName(RatingsEntity.VEHICLE) @Expose val vehicle: Float?,
                   @SerializedName(RatingsEntity.DRIVER) @Expose val driver: Float?,
                   @SerializedName(RatingsEntity.FAIR) @Expose val fair: Float?)

class CarrierModel(@SerializedName(CarrierEntity.TITLE) @Expose val title: String?,
                   @SerializedName(CarrierEntity.EMAIL) @Expose val email: String?,
                   @SerializedName(CarrierEntity.PHONE) @Expose val phone: String?,
                   @SerializedName(CarrierEntity.ID) @Expose val id: Long,
                   @SerializedName(CarrierEntity.APPROVED) @Expose val approved: Boolean,
                   @SerializedName(CarrierEntity.COMPLETED_TRANSFERS) @Expose val completedTransfers: Int,
                   @SerializedName(CarrierEntity.LANGUAGES) @Expose val languages: List<LocaleModel>,
                   @SerializedName(CarrierEntity.RATINGS) @Expose val ratings: RatingsModel,
                   @SerializedName(CarrierEntity.CAN_UPDATE_OFFERS) @Expose val canUpdateOffers: Boolean?)

class VehicleModel(name: String, regNumber: String,
                   @SerializedName(VehicleEntity.YEAR) @Expose val year: Int,
                   @SerializedName(VehicleEntity.COLOR) @Expose val color: String?,
                   @SerializedName(VehicleEntity.TRANSPORT_TYPE_ID) @Expose val transportTypeId: String,
                   @SerializedName(VehicleEntity.PAX_MAX) @Expose val paxMax: Int,
                   @SerializedName(VehicleEntity.LUGGAGE_MAX) @Expose val luggageMax: Int,
                   @SerializedName(VehicleEntity.PHOTOS) @Expose var photos: List<String>): VehicleBaseModel(name, regNumber)
