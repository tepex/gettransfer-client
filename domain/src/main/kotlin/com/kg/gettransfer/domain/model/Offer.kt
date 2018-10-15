package com.kg.gettransfer.domain.model

import java.util.Locale

data class Offer(val id: Long,
                 val status: String,
                 val wifi: Boolean,
                 val refreshments: Boolean,
                 val createdAt: String,
                 val price: Price,
                 val ratings: Ratings?,
                 val passengerFeedback: String?,
                 val carrier: Carrier,
                 val vehicle: Vehicle,
                 val driver: Profile?)

data class Price(val base: Money,
                 val percentage30: String,
                 val percentage70: String,
                 val amount: Double)

data class Ratings(val average: Double?,
                   val vehicle: Double?,
                   val driver: Double?,
                   val fair: Double?)

data class Carrier(val profile: Profile,
                   val id: Long,
                   val approved: Boolean,
                   val completedTransfers: Int,
                   val languages: List<Locale>,
                   val ratings: Ratings,
                   val canUpdateOffers: Boolean?)

data class Vehicle(val name: String,
                   val registrationNumber: String,
                   val year: Int,
                   val color: String,
                   val transportTypeId: String,
                   val paxMax: Int,
                   val luggageMax: Int,
                   val photos: List<String>)
