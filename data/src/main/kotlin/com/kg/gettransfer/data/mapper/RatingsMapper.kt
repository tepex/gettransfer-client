package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.RatingsEntity

import com.kg.gettransfer.domain.model.Ratings

/**
 * Map a [RatingsEntity] to and from a [Ratings] instance when data is moving between
 * this later and the Domain layer.
 */
open class RatingsMapper(): Mapper<RatingsEntity, Ratings> {
    /**
     * Map a [RatingsEntity] instance to a [Ratings] instance.
     */
    override fun fromEntity(type: RatingsEntity) = Ratings(type.average, type.vehicle, type.driver, type.fair)
    /**
     * Map a [Ratings] instance to a [RatingsEntity] instance.
     */
    override fun toEntity(type: Ratings) = RatingsEntity(type.average, type.vehicle, type.driver, type.fair)
}
