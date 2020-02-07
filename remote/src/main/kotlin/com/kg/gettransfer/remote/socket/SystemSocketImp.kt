package com.kg.gettransfer.remote.socket

import com.kg.gettransfer.data.socket.SystemDataStoreReceiver
import com.kg.gettransfer.data.socket.SystemEventEmitter

import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject

class SystemSocketImp : SystemEventEmitter, KoinComponent {

    private val socketManager: SocketManager = get()
    private val eventReceiver: SystemDataStoreReceiver by inject()

    override fun connectSocket() = socketManager.startConnection()

    override fun disconnectSocket() = socketManager.disconnect(false)

    fun onConnected() = eventReceiver.socketConnected()

    fun onDisconnected() = eventReceiver.socketDisconnected()
}
