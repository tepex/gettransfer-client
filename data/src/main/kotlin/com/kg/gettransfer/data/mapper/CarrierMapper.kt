package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.CarrierEntity

import com.kg.gettransfer.domain.model.Carrier
import com.kg.gettransfer.domain.model.Profile

/**
 * Map a [CarrierEntity] to and from a [Carrier] instance when data is moving between this later and the Domain layer.
 */
open class CarrierMapper(private val localeMapper: LocaleMapper,
                         private val ratingsMapper: RatingsMapper): Mapper<CarrierEntity, Carrier> {
    /**
     * Map a [CarrierEntity] instance to a [Carrier] instance.
     */
    override fun fromEntity(type: CarrierEntity) =
        Carrier(type.id,
                Profile(type.title, type.email, type.phone),
                type.approved,
                type.completedTransfers,
                type.languages.map { localeMapper.fromEntity(it) },
                ratingsMapper.fromEntity(type.ratings),
                type.canUpdateOffers ?: false)
    /**
     * Map a [Carrier] instance to a [CarrierEntity] instance.
     */
    override fun toEntity(type: Carrier): CarrierEntity { throw UnsupportedOperationException() }
}
