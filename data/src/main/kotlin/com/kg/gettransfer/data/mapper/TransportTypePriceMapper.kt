package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.TransportTypePriceEntity

import com.kg.gettransfer.domain.model.TransportTypePrice

import org.koin.standalone.get

/**
 * Map a [TransportTypePriceEntity] to and from a [TransportTypePrice] instance when data is moving between
 * this later and the Domain layer.
 */
open class TransportTypePriceMapper : Mapper<TransportTypePriceEntity, TransportTypePrice> {
    /**
     * Map a [TransportTypePriceEntity] instance to a [TransportTypePrice] instance.
     */
    override fun fromEntity(type: TransportTypePriceEntity) =
        TransportTypePrice(
            transportTypeId = type.transportTypeId,
            minFloat = type.minFloat,
            min = type.min,
            bookNow = type.bookNow
        )
    /**
     * Map a [TransportTypePrice] instance to a [TransportTypePriceEntity] instance.
     */
    override fun toEntity(type: TransportTypePrice) =
        TransportTypePriceEntity(
            transportTypeId = type.transportTypeId,
            minFloat = type.minFloat,
            min = type.min,
            bookNow = type.bookNow
        )
}
