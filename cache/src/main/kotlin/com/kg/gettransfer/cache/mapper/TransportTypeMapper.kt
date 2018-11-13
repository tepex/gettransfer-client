package com.kg.gettransfer.cache.mapper

import com.kg.gettransfer.cache.model.TransportTypeCached

import com.kg.gettransfer.data.model.TransportTypeEntity

class TransportTypeEntityMapper: EntityMapper<TransportTypeCached, TransportTypeEntity> {
    override fun fromCached(type: TransportTypeCached) = TransportTypeEntity(type.id, type.paxMax, type.luggageMax)
    override fun toCached(type: TransportTypeEntity) = TransportTypeCached(type.id, type.paxMax, type.luggageMax)
}
