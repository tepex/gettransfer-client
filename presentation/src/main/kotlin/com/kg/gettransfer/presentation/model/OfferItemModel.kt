package com.kg.gettransfer.presentation.model

import com.kg.gettransfer.domain.model.BookNowOffer
import com.kg.gettransfer.domain.model.Money
import com.kg.gettransfer.domain.model.Price
import com.kg.gettransfer.domain.model.Ratings

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
    val carrier: CarrierModel,
    val vehicle: VehicleModel,
    val driver: ProfileModel?,
    val phoneToCall: String?,
    val wheelchair: Boolean,
    val armored: Boolean
) : OfferItemModel()

fun BookNowOffer.map() = BookNowOfferModel(amount, base, withoutDiscount, transportType.map())
