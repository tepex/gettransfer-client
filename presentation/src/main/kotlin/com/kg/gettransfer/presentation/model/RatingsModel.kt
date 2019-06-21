package com.kg.gettransfer.presentation.model

import com.kg.gettransfer.domain.model.Ratings

data class RatingsModel(
    val vehicle: Double,
    val driver:  Double,
    val fair:    Double
) {

    companion object {
        //val BOOK_NOW_RATING = RatingsModel(4.5, 0.0, 0.0, 0.0)
        val BOOK_NOW_RATING = RatingsModel(0.0, 0.0, 0.0)
    }
}

fun Ratings.map() = RatingsModel(vehicle.formatRating(), driver.formatRating(), fair.formatRating())

fun Double.formatRating() = toBigDecimal().setScale(1, RoundingMode.HALF_EVEN).toDouble()
