package com.kg.gettransfer.sys.data

import com.kg.gettransfer.core.data.WriteableDataSource
import com.kg.gettransfer.sys.domain.Endpoint

import com.kg.gettransfer.sys.domain.EndpointRepository

class EndpointRepositoryImpl(
    private val remote: WriteableDataSource<EndpointEntity>
) : EndpointRepository {

    override suspend fun put(value: Endpoint) {
        remote.put(value.map())
    }
}
