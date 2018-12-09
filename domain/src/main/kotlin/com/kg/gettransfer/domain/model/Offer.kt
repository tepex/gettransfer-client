package com.kg.gettransfer.domain.model

import java.util.Date

data class Offer(
    val id: Long,
    val transferId: Long,
    val status: String,
    val wifi: Boolean,
    val refreshments: Boolean,
    val createdAt: Date,
    val updatedAt: Date?,
    val price: Price,
    val ratings: Ratings?,
    val passengerFeedback: String?,
    val carrier: Carrier,
    val vehicle: Vehicle,
    val driver: Profile?
) {

    val phoneToCall = when {
        driver?.phone != null -> driver.phone
        carrier.profile?.phone != null -> carrier.profile.phone
        else -> null
    }

    companion object {
        @JvmField
        val STATUS_NEW = "new"
        @JvmField
        val STATUS_PERFORMED = "performed"
        @JvmField
        val STATUS_BLOCKED = "blocked"
        @JvmField
        val STATUS_CANCELED = "canceled"
    }
}
