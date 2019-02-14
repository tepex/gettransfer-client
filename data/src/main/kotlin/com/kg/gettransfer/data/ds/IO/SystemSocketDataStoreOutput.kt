package com.kg.gettransfer.data.ds.IO

import com.kg.gettransfer.data.model.EndpointEntity
import com.kg.gettransfer.data.socket.SystemEventEmitter
import org.slf4j.LoggerFactory

class SystemSocketDataStoreOutput(private val emitter: SystemEventEmitter) {

     fun connectSocket(endPoint: EndpointEntity, token: String) = emitter.connectSocket(endPoint, token)
     fun changeConnection(endPoint: EndpointEntity, token: String)  = emitter.changeConnection(endPoint, token)
     fun disconnectSocket() = emitter.disconnectSocket()
     fun getSocketStatus() = emitter.getSocketStatus()
}