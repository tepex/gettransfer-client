package com.kg.gettransfer.sys.remote

import com.kg.gettransfer.remote.ApiCore
import com.kg.gettransfer.remote.socket.SocketManager
import com.kg.gettransfer.sys.data.EndpointEntity

import com.kg.gettransfer.sys.data.EndpointRemoteDataSource

import org.koin.core.KoinComponent
import org.koin.core.inject

class EndpointRemote : EndpointRemoteDataSource, KoinComponent {

    private val apiWrapper: SystemApiWrapper by inject()
    private val apiCore: ApiCore by inject()
    private val socketManager: SocketManager by inject()

    override suspend fun put(data: EndpointEntity) {
        apiWrapper.changeEndpoint(data.url)
        apiCore.changeEndpoint(data)
        socketManager.changeEndpoint(data.url)
    }

    override suspend fun clear() {
        error("Method `clear()` is prohibited in EndpointRemote")
    }
}
