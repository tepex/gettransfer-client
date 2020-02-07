package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.ds.io.SystemSocketDataStoreOutput

import com.kg.gettransfer.domain.eventListeners.SocketEventListener
import com.kg.gettransfer.domain.repository.SocketRepository

import org.koin.core.KoinComponent

class SocketRepositoryImpl(
    private val socketDataStore: SystemSocketDataStoreOutput
) : SocketRepository, KoinComponent {

    private val socketListeners = mutableSetOf<SocketEventListener>()

    override fun addSocketListener(listener: SocketEventListener)    { socketListeners.add(listener) }
    override fun removeSocketListener(listener: SocketEventListener) { socketListeners.remove(listener) }

    override fun connectSocket() = socketDataStore.connectSocket()
    override fun disconnectSocket() = socketDataStore.disconnectSocket()

    fun notifyAboutConnection()    = socketListeners.forEach { it.onSocketConnected() }
    fun notifyAboutDisconnection() = socketListeners.forEach { it.onSocketDisconnected() }
}
