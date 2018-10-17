package com.kg.gettransfer.domain.model

import java.util.Date
import java.util.Locale

data class Offer(val id: Long,
                 val status: String,
                 val wifi: Boolean,
                 val refreshments: Boolean,
                 val createdAt: Date,
                 val price: Price,
                 val ratings: Ratings?,
                 val passengerFeedback: String?,
                 val carrier: Carrier,
                 val vehicle: Vehicle,
                 val driver: Profile?) {
    companion object {
        @JvmField val STATUS_NEW       = "new"
        @JvmField val STATUS_PERFORMED = "performed"
        @JvmField val STATUS_BLOCKED   = "blocked"
        @JvmField val STATUS_CANCELED  = "canceled"
    }
}

data class Price(val base: Money,
                 val percentage30: String,
                 val percentage70: String,
                 val amount: Double) // Double !!!

data class Ratings(val average: Float?,
                   val vehicle: Float?,
                   val driver: Float?,
                   val fair: Float?)

data class Carrier(val id: Long,
                   val profile: Profile,
                   val approved: Boolean,
                   val completedTransfers: Int,
                   val languages: List<Locale>,
                   val ratings: Ratings,
                   val canUpdateOffers: Boolean)
