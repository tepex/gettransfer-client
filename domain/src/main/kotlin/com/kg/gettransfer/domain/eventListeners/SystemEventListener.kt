package com.kg.gettransfer.domain.eventListeners

interface SystemEventListener {
    fun onSocketConnected()
    fun onSocketDisconnected()
}