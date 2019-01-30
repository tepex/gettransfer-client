package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.model.EndpointEntity
import com.kg.gettransfer.data.socket.SystemEventEmitter
import org.slf4j.LoggerFactory

class SystemDataStoreIO(private val emitter: SystemEventEmitter) {
     private val log = LoggerFactory.getLogger("DataStore-IO")

     fun connectSocket(endPoint: EndpointEntity, token: String) = emitter.connectSocket(endPoint, token)

     fun changeConnection(endPoint: EndpointEntity, token: String)  = emitter.changeConnection(endPoint, token)

     fun disconnectSocket() = emitter.disconnectSocket()

}