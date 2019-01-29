package com.kg.gettransfer.remote.socket

import com.kg.gettransfer.data.model.EndpointEntity
import com.kg.gettransfer.remote.mapper.EndpointMapper
import com.kg.gettransfer.remote.socket.emitters.SystemSocketEmitter
import org.koin.standalone.KoinComponent
import org.koin.standalone.get

class SystemEventImp(private  val socketManager: SocketManager): SystemSocketEmitter, KoinComponent {
    private val endPointMapper: EndpointMapper = get()

    override fun connectSocket(endPoint: EndpointEntity, s: String) = socketManager.connect(endPointMapper.toRemote(endPoint), s)
    override fun changeConnection(endPoint: EndpointEntity, s: String) = socketManager.connectionChanged(endPointMapper.toRemote(endPoint), s)
    override fun disconnectSocket() = socketManager.disconnect()
}