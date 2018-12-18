package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.TransportTypePriceEntity

import com.kg.gettransfer.remote.model.TransportTypePriceModel

/**
 * Map a [TransportTypePriceModel] from a [TransportTypePriceEntity] instance when data is moving between this later and the Data layer.
 */
open class TransportTypePriceMapper(): EntityMapper<TransportTypePriceModel, TransportTypePriceEntity> {
    override fun fromRemote(type: TransportTypePriceModel) = TransportTypePriceEntity(type.transferId!!, type.minFloat, type.min)
    override fun toRemote(type: TransportTypePriceEntity): TransportTypePriceModel { throw UnsupportedOperationException() }
}
