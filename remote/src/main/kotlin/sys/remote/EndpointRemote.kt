package com.kg.gettransfer.sys.remote

import com.kg.gettransfer.core.data.ResultData

import com.kg.gettransfer.sys.data.EndpointRemoteDataSource

import org.koin.core.KoinComponent
import org.koin.core.inject

class EndpointRemote : EndpointRemoteDataSource, KoinComponent {

    private val apiWrapper: SystemApiWrapper by inject()

    override suspend fun put(data: String) {
        apiWrapper.changeEndpoint(data)
    }

    override suspend fun clear() {
        error("Method `clear()` is prohibited in EndpointRemote")
    }
}
