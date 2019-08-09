package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.PreferencesCache
import com.kg.gettransfer.data.PreferencesListener
import com.kg.gettransfer.data.ds.io.SystemSocketDataStoreOutput

import com.kg.gettransfer.domain.eventListeners.SocketEventListener
import com.kg.gettransfer.domain.repository.SocketRepository

import com.kg.gettransfer.sys.data.EndpointEntity
import com.kg.gettransfer.sys.data.map
import com.kg.gettransfer.sys.domain.Endpoint

import org.koin.core.KoinComponent
import org.koin.core.get

class SocketRepositoryImpl(
    private val socketDataStore: SystemSocketDataStoreOutput
) : SocketRepository, PreferencesListener, KoinComponent {

    private val preferencesCache = get<PreferencesCache>()

    init {
        preferencesCache.addListener(this)
    }

    /*
    override val accessToken: String
        get() = preferencesCache.accessToken

    override val endpoint: Endpoint
        get() = preferencesCache.endpoint.map()
*/
    private val socketListeners = mutableSetOf<SocketEventListener>()

    override fun accessTokenChanged(accessToken: String) {
        // connectionChanged()
    }
    override fun endpointChanged(endpointEntity: EndpointEntity) {}

    override fun addSocketListener(listener: SocketEventListener)    { socketListeners.add(listener) }
    override fun removeSocketListener(listener: SocketEventListener) { socketListeners.remove(listener) }

    override fun connectSocket(endpoint: Endpoint, accessToken: String) =
        socketDataStore.connectSocket(endpoint.map(), accessToken)
    override fun connectionChanged(endpoint: Endpoint, accessToken: String) =
        socketDataStore.changeConnection(endpoint.map(), accessToken)
    override fun disconnectSocket() = socketDataStore.disconnectSocket()

    fun notifyAboutConnection()    = socketListeners.forEach { it.onSocketConnected() }
    fun notifyAboutDisconnection() = socketListeners.forEach { it.onSocketDisconnected() }
}
