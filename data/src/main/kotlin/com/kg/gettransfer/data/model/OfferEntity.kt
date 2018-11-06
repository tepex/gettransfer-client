package com.kg.gettransfer.data.model

import java.util.Locale

import kotlinx.serialization.Optional
import kotlinx.serialization.Serializable

@Serializable
data class OfferEntity(val id: Long,
                       val status: String,
                       val wifi: Boolean,
                       val refreshments: Boolean,
                       val created_at: String,
                       val updated_at: String?,
                       val price: PriceEntity,
                       val ratings: RatingsEntity?,
                       @Optional val passenger_feedback: String? = null,
                       val carrier: CarrierEntity,
                       val vehicle: VehicleEntity,
                       @Optional val driver: ProfileEntity? = null)

@Serializable
data class PriceEntity(val base: MoneyEntity,
                       val percentage_30: String,
                       val percentage_70: String,
                       val amount: Double)

@Serializable
data class RatingsEntity(val average: Float?,
                         val vehicle: Float?,
                         val driver: Float?,
                         val fair: Float?)

@Serializable
data class CarrierEntity(val id: Long,
                         @Optional val title: String? = null,
                         @Optional val email: String? = null,
                         @Optional val phone: String? = null,
                         val approved: Boolean,
                         val completed_transfers: Int,
                         val languages: List<LocaleEntity>,
                         val ratings: RatingsEntity,
                         @Optional val canUpdateOffers: Boolean? = false)

@Serializable
data class VehicleEntity(val name: String,
                         val registration_number: String,
                         val year: Int,
                         val color: String?,
                         val transport_type_id: String,
                         val pax_max: Int,
                         val luggage_max: Int,
                         val photos: List<String>)
