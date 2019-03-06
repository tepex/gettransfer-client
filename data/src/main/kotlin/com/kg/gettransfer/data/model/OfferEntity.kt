package com.kg.gettransfer.data.model

import kotlinx.serialization.Optional
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class OfferEntity(
    @SerialName(ID)                    val id: Long,
    @Optional @SerialName(TRANSFER_ID) var transferId: Long? = null,
    @SerialName(STATUS)                val status: String,
    @SerialName(WIFI)                  val wifi: Boolean,
    @SerialName(REFRESHMENTS)          val refreshments: Boolean,
    @SerialName(CHARGER)               val charger: Boolean,
    @SerialName(CREATED_AT)            val createdAt: String,
    @SerialName(UPDATED_AT)            val updatedAt: String?,
    @SerialName(PRICE)                 val price: PriceEntity,
    @SerialName(RATINGS)               val ratings: RatingsEntity?,
    @Optional @SerialName(PASSENGER_FEEDBACK) val passengerFeedback: String? = null,
    @SerialName(CARRIER)               val carrier: CarrierEntity,
    @SerialName(VEHICLE)               val vehicle: VehicleEntity,
    @Optional @SerialName(DRIVER)      val driver: ProfileEntity? = null
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
        const val CHARGER            = "charger"
    }
}
