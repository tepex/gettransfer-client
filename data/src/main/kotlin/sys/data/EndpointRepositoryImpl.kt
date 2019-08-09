package com.kg.gettransfer.sys.data

import com.kg.gettransfer.core.data.WriteableDataSource
import com.kg.gettransfer.core.domain.Result

import com.kg.gettransfer.sys.domain.EndpointRepository

class EndpointRepositoryImpl(
    private val remote: WriteableDataSource<String>
) : EndpointRepository {

    override suspend fun put(value: String) {
        remote.put(value)
    }
}
