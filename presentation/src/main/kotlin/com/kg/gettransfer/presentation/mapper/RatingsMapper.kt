package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.Ratings

import com.kg.gettransfer.presentation.model.RatingsModel
import java.math.RoundingMode

open class RatingsMapper : Mapper<RatingsModel, Ratings> {
    override fun toView(type: Ratings) =
        RatingsModel(
                average = formatRating(type.average),
                vehicle = formatRating(type.vehicle),
                driver = formatRating(type.driver),
                fair = formatRating(type.fair)
        )

    override fun fromView(type: RatingsModel): Ratings { throw UnsupportedOperationException() }

    private fun formatRating(value: Float?) = value?.toBigDecimal()?.setScale(1, RoundingMode.HALF_EVEN)?.toFloat()
}
