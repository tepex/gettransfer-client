package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.CarrierEntity

import com.kg.gettransfer.domain.model.Carrier
import com.kg.gettransfer.domain.model.Profile

import org.koin.standalone.get

/**
 * Map a [CarrierEntity] to and from a [Carrier] instance when data is moving between this later and the Domain layer.
 */
open class CarrierMapper : Mapper<CarrierEntity, Carrier> {
    private val localeMapper = get<LocaleMapper>()
    private val ratingsMapper = get<RatingsMapper>()
    private val profileMapper = get<ProfileMapper>()
    /**
     * Map a [CarrierEntity] instance to a [Carrier] instance.
     */
    override fun fromEntity(type: CarrierEntity) =
        Carrier(
            type.id,
            type.profile?.let { profileMapper.fromEntity(it) },
            type.approved,
            type.completedTransfers,
            type.languages.map { localeMapper.fromEntity(it) },
            ratingsMapper.fromEntity(type.ratings),
            type.canUpdateOffers ?: false
        )
    /**
     * Map a [Carrier] instance to a [CarrierEntity] instance.
     */
    override fun toEntity(type: Carrier): CarrierEntity { throw UnsupportedOperationException() }
}
