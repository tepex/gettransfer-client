package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.CardGatewaysEntity

import com.kg.gettransfer.domain.model.CardGateways
/**
 * Map a [CardGatewaysEntity] to and from a [CardGateways] instance when data is moving between this later and the Domain layer.
 */
open class CardGatewaysMapper(): Mapper<CardGatewaysEntity, CardGateways> {
    override fun fromEntity(type: CardGatewaysEntity) = CardGateways(type.default, type.countryCode)
    override fun toEntity(type: CardGateways) = CardGatewaysEntity(type.default, type.countryCode)
}
