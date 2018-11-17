package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.CarrierEntity

import com.kg.gettransfer.domain.model.Carrier
import com.kg.gettransfer.domain.model.Profile

import org.koin.standalone.inject

/**
 * Map a [CarrierEntity] to and from a [Carrier] instance when data is moving between this later and the Domain layer.
 */
open class CarrierMapper: Mapper<CarrierEntity, Carrier> {
    private val localeMapper: LocaleMapper by inject()
    private val ratingsMapper: RatingsMapper by inject()
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
