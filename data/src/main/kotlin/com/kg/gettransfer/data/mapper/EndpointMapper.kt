package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.EndpointEntity

import com.kg.gettransfer.domain.model.Endpoint

/**
 * Map a [EndpointEntity] to and from a [Endpoint] instance when data is moving between
 * this later and the Domain layer.
 */
open class EndpointMapper : Mapper<EndpointEntity, Endpoint> {
    /**
     * Map a [EndpointEntity] instance to a [Endpoint] instance.
     */
    override fun fromEntity(type: EndpointEntity) =
        Endpoint(
            name = type.name,
            key = type.key,
            url = type.url,
            isDemo = type.isDemo
        )

    /**
     * Map a [Endpoint] instance to a [EndpointEntity] instance.
     */
    override fun toEntity(type: Endpoint) =
        EndpointEntity(
            name = type.name,
            key = type.key,
            url = type.url,
            isDemo = type.isDemo
        )
}
