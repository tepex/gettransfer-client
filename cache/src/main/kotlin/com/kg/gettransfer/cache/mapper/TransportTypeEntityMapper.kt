package com.kg.gettransfer.cache.mapper

import com.kg.gettransfer.cache.model.TransportTypeCached

import com.kg.gettransfer.data.model.TransportTypeEntity

class TransportTypeEntityMapper : EntityMapper<TransportTypeCached, TransportTypeEntity> {
    override fun fromCached(type: TransportTypeCached) =
        TransportTypeEntity(
            id         = type.id,
            paxMax     = type.paxMax,
            luggageMax = type.luggageMax
        )

    override fun toCached(type: TransportTypeEntity) =
        TransportTypeCached(
            id         = type.id,
            paxMax     = type.paxMax,
            luggageMax = type.luggageMax
        )
}
