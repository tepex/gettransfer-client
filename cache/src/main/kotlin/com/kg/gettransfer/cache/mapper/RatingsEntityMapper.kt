package com.kg.gettransfer.cache.mapper

import com.kg.gettransfer.cache.model.RatingsCached
import com.kg.gettransfer.data.model.RatingsEntity

open class RatingsEntityMapper : EntityMapper<RatingsCached, RatingsEntity> {

    override fun fromCached(type: RatingsCached) =
            RatingsEntity(
                    average = type.average,
                    vehicle = type.vehicle,
                    driver = type.driver,
                    fair = type.fair
            )

    override fun toCached(type: RatingsEntity) =
            RatingsCached(
                    average = type.average,
                    vehicle = type.vehicle,
                    driver = type.driver,
                    fair = type.fair
            )
}