package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.TransportTypeEntity

import com.kg.gettransfer.remote.model.TransportTypeModel

/**
 * Map a [TransportTypeEntity] from a [TransportTypeModel] instance when data is moving between this later and the Data layer.
 */
open class TransportTypeMapper(): EntityMapper<TransportTypeModel, TransportTypeEntity> {
    override fun fromRemote(type: TransportTypeModel) = TransportTypeEntity(type.id, type.paxMax, type.luggageMax)
    override fun toRemote(type: TransportTypeEntity): TransportTypeModel { throw UnsupportedOperationException() }
}
