package com.kg.gettransfer.data.ds.io

import com.kg.gettransfer.sys.data.EndpointEntity
import com.kg.gettransfer.data.socket.SystemEventEmitter

class SystemSocketDataStoreOutput(private val emitter: SystemEventEmitter) {

    fun connectSocket(endPoint: EndpointEntity, token: String) = emitter.connectSocket(endPoint, token)

    fun changeConnection(endPoint: EndpointEntity, token: String) = emitter.changeConnection(endPoint, token)

    fun disconnectSocket() = emitter.disconnectSocket()
}
