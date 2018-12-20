package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.EndpointEntity

import com.kg.gettransfer.remote.model.EndpointModel

/**
 * Map a [EndpointModel] from an [EndpointEntity] instance when data is moving between this later and the Data layer.
 */
open class EndpointMapper : EntityMapper<EndpointModel, EndpointEntity> {

    override fun fromRemote(type: EndpointModel): EndpointEntity { throw UnsupportedOperationException() }

    override fun toRemote(type: EndpointEntity) =
        EndpointModel(
            key = type.key,
            url = type.url
        )
}
