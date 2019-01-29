package com.kg.gettransfer.data.socket

import com.kg.gettransfer.data.model.EndpointEntity

interface SystemEventEmitter {
    fun changeConnection(endPoint: EndpointEntity, s: String)
    fun connectSocket(endPoint: EndpointEntity, s: String)
    fun disconnectSocket()
}