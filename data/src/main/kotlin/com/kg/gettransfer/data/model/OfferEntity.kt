package com.kg.gettransfer.data.model

import java.util.Locale

data class OfferEntity(val id: Long,
                       val status: String,
                       val wifi: Boolean,
                       val refreshments: Boolean,
                       val createdAt: String,
                       val price: PriceEntity,
                       val ratings: RatingsEntity?,
                       val passengerFeedback: String?,
                       val carrier: CarrierEntity,
                       val vehicle: VehicleEntity,
                       val driver: ProfileEntity?)

data class PriceEntity(val base: MoneyEntity,
                       val percentage30: String,
                       val percentage70: String,
                       val amount: Double)

data class RatingsEntity(val average: Float?,
                         val vehicle: Float?,
                         val driver: Float?,
                         val fair: Float?)

data class CarrierEntity(val id: Long,
                         val profile: ProfileEntity,
                         val approved: Boolean,
                         val completedTransfers: Int,
                         val languages: List<LocaleEntity>,
                         val ratings: RatingsEntity,
                         val canUpdateOffers: Boolean?)

data class VehicleEntity(val vehicleBase: VehicleBaseEntity,
                         val year: Int,
                         val color: String?,
                         val transportType: TransportTypeEntity,
                         val photos: List<String>)
