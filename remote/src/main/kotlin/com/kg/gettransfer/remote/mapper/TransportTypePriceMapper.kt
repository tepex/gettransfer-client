package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.TransportTypePriceEntity

import com.kg.gettransfer.remote.model.TransportTypePriceModel

/**
 * Map a [TransportTypePriceModel] from a [TransportTypePriceEntity] instance when data is moving between this later and the Data layer.
 */
open class TransportTypePriceMapper : EntityMapper<Map.Entry<String, TransportTypePriceModel>, TransportTypePriceEntity> {

    override fun fromRemote(type: Map.Entry<String, TransportTypePriceModel>): TransportTypePriceEntity =
        TransportTypePriceEntity(
            minFloat = type.value.minFloat,
            min = type.value.min,
            bookNow = type.value.bookNow
        )

    override fun toRemote(type: TransportTypePriceEntity): Map.Entry<String, TransportTypePriceModel> { throw UnsupportedOperationException() }
}
