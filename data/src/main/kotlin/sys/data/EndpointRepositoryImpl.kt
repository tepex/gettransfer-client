package com.kg.gettransfer.sys.data

import com.kg.gettransfer.core.data.WriteableDataSource
import com.kg.gettransfer.data.PreferencesCache
import com.kg.gettransfer.sys.domain.Endpoint

import com.kg.gettransfer.sys.domain.EndpointRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class EndpointRepositoryImpl(
    private val remote: WriteableDataSource<EndpointEntity>
) : EndpointRepository, KoinComponent {

    private val preferencesCache: PreferencesCache by inject()

    override suspend fun put(value: Endpoint) {
        remote.put(value.map())
        preferencesCache.endpointUrl = value.url
    }
}
