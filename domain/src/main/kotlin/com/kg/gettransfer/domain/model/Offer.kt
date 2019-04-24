package com.kg.gettransfer.domain.model

import java.util.Date

data class Offer(
    override val id: Long,
    val transferId: Long,
    val status: String,
    val wifi: Boolean,
    val refreshments: Boolean,
    val charger: Boolean,
    val createdAt: Date,
    val updatedAt: Date?,
    val price: Price,
    val ratings: Ratings?,
    val passengerFeedback: String?,
    val carrier: Carrier,
    val vehicle: Vehicle,
    val driver: Profile?
) : Entity() {

    val phoneToCall = when {
        driver?.phone != null          -> driver.phone
        carrier.profile?.phone != null -> carrier.profile.phone
        else                           -> null
    }

    fun isRateAvailable() = ratings != null   //call this first

    fun isOfferRatedByUser() =                //call after check
        ratings?.run {
            vehicle != NO_RATE && driver != NO_RATE && fair != NO_RATE
        } ?: true

    companion object {
        const val STATUS_NEW       = "new"
        const val STATUS_PERFORMED = "performed"
        const val STATUS_BLOCKED   = "blocked"
        const val STATUS_CANCELED  = "canceled"

        const val NO_RATE          = 0f
    }
}
