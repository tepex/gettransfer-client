package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.eventListeners.SocketEventListener
import com.kg.gettransfer.domain.repository.SocketRepository

import com.kg.gettransfer.sys.domain.Endpoint
import com.kg.gettransfer.sys.domain.GetPreferencesInteractor

class SocketInteractor(
    private val socketRepository: SocketRepository,
    private val getPreferences: GetPreferencesInteractor
) {
    suspend fun openSocketConnection() =
        socketRepository.connectSocket(
            getPreferences().getModel().endpoint!!,
            getPreferences().getModel().accessToken
        )

    fun closeSocketConnection() = socketRepository.disconnectSocket()

    fun addSocketListener(listener: SocketEventListener)    { socketRepository.addSocketListener(listener) }
    fun removeSocketListener(listener: SocketEventListener) { socketRepository.removeSocketListener(listener) }
}
