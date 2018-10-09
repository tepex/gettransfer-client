package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.TransportTypeEntity

import com.kg.gettransfer.domain.model.TransportType
/**
 * Map a [TransportTypeEntity] to and from a [TransportType] instance when data is moving between this later and the Domain layer.
 */
open class TransportTypeMapper(): Mapper<TransportTypeEntity, TransportType> {
    override fun fromEntity(type: TransportTypeEntity) = TransportType(type.id, type.paxMax, type.luggageMax)
    override fun toEntity(type: TransportType) = TransportTypeEntity(type.id, type.paxMax, type.luggageMax)
}
