package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.eventListeners.SocketEventListener

interface SocketRepository {
    /*
    val accessToken: String
    val endpoint: Endpoint
    */

    fun connectSocket()
    fun disconnectSocket()
    fun connectionChanged()

    fun addSocketListener(listener: SocketEventListener)
    fun removeSocketListener(listener: SocketEventListener)
}
