package com.kg.gettransfer.data.model

import java.util.Locale

import kotlinx.serialization.Optional
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class OfferEntity(
    @SerialName(ID) val id: Long,
    @SerialName(TRANSFER_ID) val transferId: Long,
    @SerialName(STATUS) val status: String,
    @SerialName(WIFI) val wifi: Boolean,
    @SerialName(REFRESHMENTS) val refreshments: Boolean,
    @SerialName(CREATED_AT) val createdAt: String,
    @SerialName(UPDATED_AT) val updatedAt: String?,
    @SerialName(PRICE) val price: PriceEntity,
    @SerialName(RATINGS) val ratings: RatingsEntity?,
    @Optional @SerialName(PASSENGER_FEEDBACK) val passengerFeedback: String? = null,
    @SerialName(CARRIER) val carrier: CarrierEntity,
    @SerialName(VEHICLE) val vehicle: VehicleEntity,
    @Optional @SerialName(DRIVER) val driver: ProfileEntity? = null
) {

    companion object {
        const val ENTITY_NAME        = "offer"
        const val ID                 = "id"
        const val TRANSFER_ID        = "transfer_id"
        const val STATUS             = "status"
        const val WIFI               = "wifi"
        const val REFRESHMENTS       = "refreshments"
        const val CREATED_AT         = "created_at"
        const val UPDATED_AT         = "updated_at"
        const val PRICE              = "price"
        const val RATINGS            = "ratings"
        const val PASSENGER_FEEDBACK = "passenger_feedback"
        const val CARRIER            = "carrier"
        const val VEHICLE            = "vehicle"
        const val DRIVER             = "driver"
    }
}

@Serializable
data class PriceEntity(
    @SerialName(BASE) val base: MoneyEntity,
    @Optional @SerialName(NO_DISCOUNT) val withoutDiscount: MoneyEntity? = null,
    @SerialName(PERCENTAGE_30) val percentage30: String,
    @SerialName(PERCENTAGE_70) val percentage70: String,
    @SerialName(AMOUNT) val amount: Double
) {

    companion object {
        const val BASE          = "base"
        const val NO_DISCOUNT   = "without_discount"
        const val PERCENTAGE_30 = "percentage_30"
        const val PERCENTAGE_70 = "percentage_70"
        const val AMOUNT        = "amount"
    }
}

@Serializable
data class RatingsEntity(
    @SerialName(AVERAGE) val average: Float?,
    @SerialName(VEHICLE) val vehicle: Float?,
    @SerialName(DRIVER) val driver:   Float?,
    @SerialName(FAIR) val fair:       Float?
) {

    companion object {
        const val AVERAGE = "average"
        const val VEHICLE = "vehicle"
        const val DRIVER  = "driver"
        const val FAIR    = "fair"
    }
}

@Serializable
data class CarrierEntity(
    @SerialName(ID) val id: Long,
    @Optional @SerialName(TITLE) val title: String? = null,
    @Optional @SerialName(EMAIL) val email: String? = null,
    @Optional @SerialName(PHONE) val phone: String? = null,
    @SerialName(APPROVED) val approved: Boolean,
    @SerialName(COMPLETED_TRANSFERS) val completedTransfers: Int,
    @SerialName(LANGUAGES) val languages: List<LocaleEntity>,
    @SerialName(RATINGS) val ratings: RatingsEntity,
    @Optional @SerialName(CAN_UPDATE_OFFERS) val canUpdateOffers: Boolean? = false
) {

    companion object {
        const val ENTITY_NAME         = "carrier"
        const val ID                  = "id"
        const val TITLE               = "title"
        const val EMAIL               = "email"
        const val PHONE               = "phone"
        const val APPROVED            = "approved"
        const val COMPLETED_TRANSFERS = "completed_transfers"
        const val LANGUAGES           = "languages"
        const val RATINGS             = "ratings"
        const val CAN_UPDATE_OFFERS   = "can_update_offers"
    }
}

@Serializable
data class VehicleEntity(
    @SerialName(NAME) val name: String,
    @SerialName(REGISTRATION_NUMBER) val registrationNumber: String,
    @SerialName(YEAR) val year: Int,
    @SerialName(COLOR) val color: String?,
    @SerialName(TRANSPORT_TYPE_ID) val transportTypeId: String,
    @SerialName(PAX_MAX) val paxMax: Int,
    @SerialName(LUGGAGE_MAX) val luggageMax: Int,
    @SerialName(PHOTOS) val photos: List<String>
) {

    companion object {
        const val NAME                = "name"
        const val REGISTRATION_NUMBER = "registration_number"
        const val YEAR                = "year"
        const val COLOR               = "color"
        const val TRANSPORT_TYPE_ID   = "transport_type_id"
        const val PAX_MAX             = "pax_max"
        const val LUGGAGE_MAX         = "luggage_max"
        const val PHOTOS              = "photos"
    }
}
