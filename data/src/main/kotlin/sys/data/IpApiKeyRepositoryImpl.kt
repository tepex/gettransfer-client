package com.kg.gettransfer.sys.data

import com.kg.gettransfer.core.data.WriteableDataSource

import com.kg.gettransfer.sys.domain.IpApiKeyRepository

class IpApiKeyRepositoryImpl(
    private val remote: WriteableDataSource<String>
) : IpApiKeyRepository {

    override suspend fun put(value: String) {
        remote.put(value)
    }
}
