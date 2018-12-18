package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.TransportTypeEntity

import com.kg.gettransfer.domain.model.TransportType
/**
 * Map a [TransportTypeEntity] to and from a [TransportType] instance when data is moving between this later and the Domain layer.
 */
open class TransportTypeMapper : Mapper<TransportTypeEntity, TransportType> {
    override fun fromEntity(type: TransportTypeEntity) =
        TransportType(
            id = type.id,
            paxMax = type.paxMax,
            luggageMax = type.luggageMax
        )

    override fun toEntity(type: TransportType) =
        TransportTypeEntity(
            id = type.id,
            paxMax = type.paxMax,
            luggageMax = type.luggageMax
        )
}
