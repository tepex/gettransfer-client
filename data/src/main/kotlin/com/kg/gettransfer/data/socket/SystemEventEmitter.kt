package com.kg.gettransfer.data.socket

interface SystemEventEmitter {

    fun changeConnection()

    fun connectSocket()

    fun disconnectSocket()
}
