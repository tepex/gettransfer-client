package com.kg.gettransfer.remote.socket

import com.kg.gettransfer.data.model.CoordinateEntity
import com.kg.gettransfer.data.socket.CoordinateDataStoreReceiver
import com.kg.gettransfer.remote.socket.emitters.CoordinateSocketEmitter
import com.kg.gettransfer.remote.socket.emitters.CoordinateSocketEmitter.Companion.INIT_LOCATION_EVENTS
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject

class CoordinateSocketImpl : CoordinateSocketEmitter, KoinComponent {

    private val eventReceiver: CoordinateDataStoreReceiver by inject()
    private val socketManager: SocketManager = get()

    override fun initLocationReceiving(transferId: Long) {
        socketManager.emitEvent(INIT_LOCATION_EVENTS, transferId)
    }

    internal fun onLocationUpdated(coordinate: CoordinateEntity) = eventReceiver.onLocationReceived(coordinate)
}
