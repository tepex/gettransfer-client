package com.kg.gettransfer.data.socket

import com.kg.gettransfer.data.model.EndpointEntity

interface SystemEventEmitter {

    fun changeConnection(endpoint: EndpointEntity, token: String)

    fun connectSocket(endpoint: EndpointEntity, token: String)

    fun disconnectSocket()
}
