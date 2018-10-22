package com.kg.gettransfer.presentation.model

import java.util.Locale

class OfferModel(val id: Long,
                 val status: String,
                 val wifi: Boolean,
                 val refreshments: Boolean,
                 val createdAt: String,
                 val price: PriceModel,
                 val ratings: RatingsModel?,
                 val passengerFeedback: String?,
                 val carrier: CarrierModel,
                 val vehicle: VehicleModel,
                 val driver: ProfileModel?) {
    companion object {
        const val FULL_PRICE = 100
        const val PRICE_30   = 30
    }
}

class PriceModel(val base: MoneyModel, val percentage30: String, val percentage70: String, val amount: String)

class RatingsModel(val average: Float?, val vehicle: Float?, val driver: Float?, val fair: Float?)

class CarrierModel(val id: Long,
                   val profile: ProfileModel,
                   val approved: Boolean,
                   val completedTransfers: Int,
                   val languages: List<LocaleModel>,
                   val ratings: RatingsModel,
                   val canUpdateOffers: Boolean = false)
