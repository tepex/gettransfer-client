package com.kg.gettransfer.presentation.model

import com.kg.gettransfer.domain.model.Ratings

import java.math.RoundingMode

data class RatingsModel(
    val average: Float,
    val vehicle: Float,
    val driver:  Float,
    val fair:    Float
) {

    companion object {
        //val BOOK_NOW_RATING = RatingsModel(4.5, 0.0, 0.0, 0.0)
        const val NO_RATING  = 0.0f
        val BOOK_NOW_RATING = RatingsModel(4.5f, NO_RATING, NO_RATING, NO_RATING)
        val EMPTY = RatingsModel(NO_RATING, NO_RATING, NO_RATING, NO_RATING)
    }
}

fun Ratings.map() =
    RatingsModel(average.formatRating(), vehicle.formatRating(), driver.formatRating(), fair.formatRating())

fun Double.formatRating() = toBigDecimal().setScale(1, RoundingMode.HALF_EVEN).toFloat()
