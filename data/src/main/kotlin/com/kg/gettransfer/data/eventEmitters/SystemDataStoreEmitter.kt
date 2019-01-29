package com.kg.gettransfer.data.eventEmitters

import com.kg.gettransfer.data.model.EndpointEntity

interface SystemDataStoreEmitter {
    fun changeConnection(endPoint: EndpointEntity, s: String)
    fun connectSocket(endPoint: EndpointEntity, s: String)
    fun disconnectSocket()
}