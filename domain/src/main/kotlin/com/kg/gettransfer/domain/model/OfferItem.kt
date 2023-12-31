package com.kg.gettransfer.domain.model

import java.util.Date

sealed class OfferItem

data class Offer(
    val id: Long,
    val transferId: Long,
    val status: String,
    val currency: String,
    val wifi: Boolean,
    val isWithNameSign: Boolean,
    val refreshments: Boolean,
    val charger: Boolean,
    val createdAt: Date,
    val updatedAt: Date?,
    val price: Price,
    val ratings: Ratings?,
    val passengerFeedback: String?,
    val carrier: Carrier,
    val vehicle: Vehicle,
    val driver: Profile?,
    val wheelchair: Boolean,
    val armored: Boolean,
    val availableUntil: Date?
) : OfferItem() {

    val phoneToCall = when {
        driver?.phone != null          -> driver.phone
        carrier.profile?.phone != null -> carrier.profile.phone
        else                           -> null
    }

    // call this first
    fun isRateAvailable() = ratings != null

    // call after check
    fun isNeededRateOffer() = ratings?.run {
        neededRateField(vehicle) || neededRateField(driver) || neededRateField(communication)
    } ?: false

    private fun neededRateField(fieldRating: Double?) = fieldRating == Ratings.NO_RATING
}

data class BookNowOffer(
    val amount: Double,
    val base: Money,
    val withoutDiscount: Money?,
    val transportType: TransportType
) : OfferItem()

fun OfferItem.getOfferId(): Long? = if (this is Offer) id else null
