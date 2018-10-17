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
                 val driver: ProfileModel?)

class PriceModel(val base: MoneyModel, val percentage30: String, val percentage70: String, val amount: String)

class RatingsModel(val average: Float?, val vehicle: Float?, val driver: Float?, val fair: Float?)

class CarrierModel(val id: Long,
                   val profile: ProfileModel,
                   val approved: Boolean,
                   val completedTransfers: Int,
                   val languages: List<LocaleModel>,
                   val ratings: RatingsModel,
                   val canUpdateOffers: Boolean = false)
