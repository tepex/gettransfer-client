package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.Ratings

import com.kg.gettransfer.presentation.model.RatingsModel

open class RatingsMapper : Mapper<RatingsModel, Ratings> {
    override fun toView(type: Ratings) =
        RatingsModel(type.average, type.vehicle, type.driver, type.fair)

    override fun fromView(type: RatingsModel): Ratings { throw UnsupportedOperationException() }
}
