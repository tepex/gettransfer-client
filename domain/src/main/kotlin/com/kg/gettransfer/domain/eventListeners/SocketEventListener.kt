package com.kg.gettransfer.domain.eventListeners

interface SocketEventListener {
    fun onSocketConnected()
    fun onSocketDisconnected()
}
