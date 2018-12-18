package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.CarrierEntity

import com.kg.gettransfer.domain.model.Carrier
import com.kg.gettransfer.domain.model.Profile

import org.koin.standalone.get

/**
 * Map a [CarrierEntity] to and from a [Carrier] instance when data is moving between this later and the Domain layer.
 */
open class CarrierMapper : Mapper<CarrierEntity, Carrier> {
    private val localeMapper  = get<LocaleMapper>()
    private val ratingsMapper = get<RatingsMapper>()
    private val profileMapper = get<ProfileMapper>()
    /**
     * Map a [CarrierEntity] instance to a [Carrier] instance.
     */
    override fun fromEntity(type: CarrierEntity) =
        Carrier(
            id                 = type.id,
            profile            = type.profile?.let { profileMapper.fromEntity(it) },
            approved           = type.approved,
            completedTransfers = type.completedTransfers,
            languages          = type.languages.map { localeMapper.fromEntity(it) },
            ratings            = ratingsMapper.fromEntity(type.ratings),
            canUpdateOffers    = type.canUpdateOffers ?: false
        )
    /**
     * Map a [Carrier] instance to a [CarrierEntity] instance.
     */
    override fun toEntity(type: Carrier): CarrierEntity { throw UnsupportedOperationException() }
}
