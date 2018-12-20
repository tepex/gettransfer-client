package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.CardGatewaysEntity

import com.kg.gettransfer.domain.model.CardGateways
/**
 * Map a [CardGatewaysEntity] to and from a [CardGateways] instance when data is moving between this later and the Domain layer.
 */
open class CardGatewaysMapper : Mapper<CardGatewaysEntity, CardGateways> {
    override fun fromEntity(type: CardGatewaysEntity) =
        CardGateways(
            def = type.def,
            countryCode = type.countryCode
        )

    override fun toEntity(type: CardGateways) =
        CardGatewaysEntity(
            def = type.def,
            countryCode = type.countryCode
        )
}
