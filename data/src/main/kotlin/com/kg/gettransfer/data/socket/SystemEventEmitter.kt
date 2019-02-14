package com.kg.gettransfer.data.socket

import com.kg.gettransfer.data.model.EndpointEntity

interface SystemEventEmitter {
    fun changeConnection(endPoint: EndpointEntity, token: String)
    fun connectSocket(endPoint: EndpointEntity, token: String)
    fun disconnectSocket()
    fun getSocketStatus(): Int
}