package com.kg.gettransfer.presentation.model

import com.kg.gettransfer.domain.model.BookNowOffer
import com.kg.gettransfer.domain.model.Carrier
import com.kg.gettransfer.domain.model.Money
import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Price
import com.kg.gettransfer.domain.model.Profile
import com.kg.gettransfer.domain.model.Ratings

import com.kg.gettransfer.presentation.ui.SystemUtils

sealed class OfferItemModel

data class BookNowOfferModel(
    val amount: Double,
    val base: Money,
    val withoutDiscount: Money?,
    var transportType: TransportTypeModel
) : OfferItemModel()

data class OfferModel(
    val id: Long,
    val transferId: Long,
    val status: String,
    val currency: String,
    val wifi: Boolean,
    val isWithNameSign: Boolean,
    val refreshments: Boolean,
    val charger: Boolean,
    val createdAt: String,
    val price: Price,
    val ratings: Ratings?,
    val passengerFeedback: String?,
    val carrier: Carrier,
    val vehicle: VehicleModel,
    val driver: Profile?,
    val phoneToCall: String?,
    val wheelchair: Boolean,
    val armored: Boolean
) : OfferItemModel()


fun BookNowOffer.map() = BookNowOfferModel(amount, base, withoutDiscount, transportType.map())

fun Offer.map() = OfferModel(
    id = id,
    transferId = transferId,
    status = status,
    currency = currency,
    wifi = wifi,
    isWithNameSign = isWithNameSign,
    refreshments = refreshments,
    charger = charger,
    createdAt = SystemUtils.formatDateTime(createdAt),
    price = price,
    ratings = ratings,
    passengerFeedback = passengerFeedback,
    carrier = carrier,
    vehicle = vehicle.map(),
    driver = driver,
    phoneToCall = phoneToCall,
    wheelchair = wheelchair,
    armored = armored
)
