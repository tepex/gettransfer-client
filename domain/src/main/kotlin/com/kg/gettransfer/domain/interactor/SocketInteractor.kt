package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.eventListeners.SocketEventListener
import com.kg.gettransfer.domain.repository.SocketRepository

class SocketInteractor(private val socketRepository: SocketRepository) {
    fun openSocketConnection() = socketRepository.connectSocket()
    fun closeSocketConnection() = socketRepository.disconnectSocket()

    fun addSocketListener(listener: SocketEventListener)    { socketRepository.addSocketListener(listener) }
    fun removeSocketListener(listener: SocketEventListener) { socketRepository.removeSocketListener(listener) }
}
