package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.model.EndpointEntity
import com.kg.gettransfer.data.socket.SystemEventEmitter
import org.slf4j.LoggerFactory

class SystemDataStoreIO(private val emitter: SystemEventEmitter) {
     protected val log = LoggerFactory.getLogger("DataStore-IO")

     fun connectSocket(endPoint: EndpointEntity, s: String) {
          log.info("MySocketConnect ds")
          emitter.connectSocket(endPoint, s)
     }

     fun changeConnection(endPoint: EndpointEntity, s: String) {}

     fun disconnectSocket() = emitter.disconnectSocket()

}