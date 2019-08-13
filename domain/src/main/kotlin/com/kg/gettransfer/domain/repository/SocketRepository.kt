package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.eventListeners.SocketEventListener
import com.kg.gettransfer.sys.domain.Endpoint

interface SocketRepository {
    /*
    val accessToken: String
    val endpoint: Endpoint
    */

    fun connectSocket(endpoint: Endpoint, accessToken: String)
    fun disconnectSocket()
    fun connectionChanged(endpoint: Endpoint, accessToken: String)

    fun addSocketListener(listener: SocketEventListener)
    fun removeSocketListener(listener: SocketEventListener)
}
