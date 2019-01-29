package com.kg.gettransfer.domain.eventEmitters

interface SystemEmitterGeneral {
    fun <E> connectSocket(endPoint: E, s: String)
    fun <E> changeConnection(endPoint: E, s: String)
    fun disconnectSocket()
}