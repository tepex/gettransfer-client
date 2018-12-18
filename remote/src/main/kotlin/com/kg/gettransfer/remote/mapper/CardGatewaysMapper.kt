package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.CardGatewaysEntity

import com.kg.gettransfer.remote.model.CardGatewaysModel

/**
 * Map a [CardGatewaysEntity] from a [CardGatewaysModel] instance when data is moving between this later and the Data layer.
 */
open class CardGatewaysMapper : EntityMapper<CardGatewaysModel, CardGatewaysEntity> {
    override fun fromRemote(type: CardGatewaysModel) =
        CardGatewaysEntity(
            def = type.def,
            countryCode = type.countryCode
        )

    override fun toRemote(type: CardGatewaysEntity): CardGatewaysModel { throw UnsupportedOperationException() }
}
