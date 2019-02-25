package com.kg.gettransfer.remote.socket

import com.kg.gettransfer.data.model.EndpointEntity
import com.kg.gettransfer.data.socket.SystemDataStoreReceiver
import com.kg.gettransfer.remote.mapper.EndpointMapper
import com.kg.gettransfer.remote.socket.emitters.SystemSocketEmitter
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import org.koin.standalone.inject

class SystemSocketImp : SystemSocketEmitter, KoinComponent {
    private val endPointMapper: EndpointMapper = get()
    private val socketManager: SocketManager = get()
    private val eventReceiver: SystemDataStoreReceiver by inject()

    override fun connectSocket(endPoint: EndpointEntity, token: String) = socketManager.startConnection(endPointMapper.toRemote(endPoint), token)
    override fun changeConnection(endPoint: EndpointEntity, token: String) = socketManager.changeConnection(endPointMapper.toRemote(endPoint), token)
    override fun disconnectSocket() = socketManager.disconnect(false)

    fun onConnected() = eventReceiver.socketConnected()
    fun onDisconnected() = eventReceiver.socketDisconnected()
}