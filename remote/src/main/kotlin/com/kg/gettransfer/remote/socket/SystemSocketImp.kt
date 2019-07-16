package com.kg.gettransfer.remote.socket

import com.kg.gettransfer.data.model.EndpointEntity
import com.kg.gettransfer.data.socket.SystemDataStoreReceiver
import com.kg.gettransfer.data.socket.SystemEventEmitter
import com.kg.gettransfer.remote.model.map
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject

class SystemSocketImp : SystemEventEmitter, KoinComponent {

    private val socketManager: SocketManager = get()
    private val eventReceiver: SystemDataStoreReceiver by inject()

    override fun connectSocket(endpoint: EndpointEntity, token: String) =
        socketManager.startConnection(endpoint.map(), token)

    override fun changeConnection(endpoint: EndpointEntity, token: String) =
        socketManager.changeConnection(endpoint.map(), token)

    override fun disconnectSocket() = socketManager.disconnect(false)

    fun onConnected() = eventReceiver.socketConnected()

    fun onDisconnected() = eventReceiver.socketDisconnected()
}
