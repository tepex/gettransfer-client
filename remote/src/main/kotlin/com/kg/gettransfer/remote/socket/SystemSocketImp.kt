package com.kg.gettransfer.remote.socket

import com.kg.gettransfer.data.model.EndpointEntity
import com.kg.gettransfer.remote.mapper.EndpointMapper
import com.kg.gettransfer.remote.socket.emitters.SystemSocketEmitter
import org.koin.standalone.KoinComponent
import org.koin.standalone.get

class SystemSocketImp : SystemSocketEmitter, KoinComponent {
    private val endPointMapper: EndpointMapper = get()
    private val socketManager: SocketManager = get()

    override fun connectSocket(endPoint: EndpointEntity, token: String) = socketManager.connect(endPointMapper.toRemote(endPoint), token)
    override fun changeConnection(endPoint: EndpointEntity, token: String) {
        with(socketManager) {
            shouldReconnect = true
            connectionChanged(endPointMapper.toRemote(endPoint), token)
        }
    }

    override fun disconnectSocket() = socketManager.disconnect()
}