package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.Offer

import java.text.DateFormat

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class OfferEntity(
    @SerialName(ID)                 val id: Long,
    @SerialName(TRANSFER_ID)        var transferId: Long? = null,
    @SerialName(STATUS)             val status: String,
    @SerialName(CURRENCY)           val currency: String,
    @SerialName(WIFI)               val wifi: Boolean,
    @SerialName(WITH_NAME_SIGN)     val isWithNameSign: Boolean? = null,
    @SerialName(REFRESHMENTS)       val refreshments: Boolean,
    @SerialName(CHARGER)            val charger: Boolean,
    @SerialName(CREATED_AT)         val createdAt: String,
    @SerialName(UPDATED_AT)         val updatedAt: String?,
    @SerialName(PRICE)              val price: PriceEntity,
    @SerialName(RATINGS)            val ratings: RatingsEntity?,
    @SerialName(PASSENGER_FEEDBACK) val passengerFeedback: String? = null,
    @SerialName(CARRIER)            val carrier: CarrierEntity,
    @SerialName(VEHICLE)            val vehicle: VehicleEntity,
    @SerialName(DRIVER)             val driver: ProfileEntity? = null,
    @SerialName(WHEELCHAIR)         val wheelchair: Boolean,
    @SerialName(ARMORED)            val armored: Boolean,
    @SerialName(AVAILABLE_UNTIL)    val availableUntil: String?
) {

    companion object {
        const val ENTITY_NAME        = "offer"
        const val ID                 = "id"
        const val TRANSFER_ID        = "transfer_id"
        const val STATUS             = "status"
        const val CURRENCY           = "currency"
        const val WIFI               = "wifi"
        const val WITH_NAME_SIGN     = "with_name_sign"
        const val REFRESHMENTS       = "refreshments"
        const val CREATED_AT         = "created_at"
        const val UPDATED_AT         = "updated_at"
        const val PRICE              = "price"
        const val RATINGS            = "ratings"
        const val PASSENGER_FEEDBACK = "passenger_feedback"
        const val CARRIER            = "carrier"
        const val VEHICLE            = "vehicle"
        const val DRIVER             = "driver"
        const val CHARGER            = "charger"
        const val WHEELCHAIR         = "wheelchair"
        const val ARMORED            = "armored"
        const val AVAILABLE_UNTIL    = "available_until"
    }
}

fun OfferEntity.map(dateFormat: DateFormat, url: String) =
    Offer(
        id,
        transferId ?: 0,
        status,
        currency,
        wifi,
        isWithNameSign ?: false,
        refreshments,
        charger,
        dateFormat.parse(createdAt),
        updatedAt?.let { dateFormat.parse(it) },
        price.map(),
        ratings?.map(),
        passengerFeedback,
        carrier.map(),
        vehicle.map(url),
        driver?.map(),
        wheelchair,
        armored,
        availableUntil?.let { dateFormat.parse(it) }
    )

fun Offer.map(dateFormat: DateFormat) =
    OfferEntity(
        id,
        transferId,
        status,
        currency,
        wifi,
        isWithNameSign,
        refreshments,
        charger,
        dateFormat.format(createdAt),
        updatedAt?.let { dateFormat.format(it) },
        price.map(),
        ratings?.map(),
        passengerFeedback,
        carrier.map(),
        vehicle.map(),
        driver?.map(),
        wheelchair,
        armored,
        availableUntil?.let { dateFormat.format(it) }
    )
