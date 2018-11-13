package com.kg.gettransfer.cache.mapper

import com.kg.gettransfer.cache.model.CardGatewaysCached

import com.kg.gettransfer.data.model.CardGatewaysEntity

class CardGatewaysEntityMapper: EntityMapper<CardGatewaysCached, CardGatewaysEntity> {
    override fun fromCached(type: CardGatewaysCached) = CardGatewaysEntity(type.def, type.countryCode)
    override fun toCached(type: CardGatewaysEntity) = CardGatewaysCached(type.default, type.countryCode)
}
