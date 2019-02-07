package com.kg.gettransfer.remote.socket

import com.kg.gettransfer.data.model.CoordinateEntity
import com.kg.gettransfer.data.socket.TransferDataStoreReceiver
import com.kg.gettransfer.remote.socket.emitters.TransferSocketEmitter
import com.kg.gettransfer.remote.socket.emitters.TransferSocketEmitter.Companion.INIT_LOCATION_EVENTS
import com.kg.gettransfer.remote.socket.emitters.TransferSocketEmitter.Companion.OWN_LOCATION
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import org.koin.standalone.inject

class TransferSocketImpl: TransferSocketEmitter, KoinComponent {
    private val eventReceiver: TransferDataStoreReceiver by inject()
    private val socketManager: SocketManager = get()

    override fun initLocationReceiving() = socketManager.emitAck(INIT_LOCATION_EVENTS, arrayOf(2227))

    override fun sendOwnLocation(coordinate: CoordinateEntity) = socketManager.emitEvent(OWN_LOCATION, arrayOf(coordinate))

    internal fun onLocationUpdated(coordinate: CoordinateEntity) = eventReceiver.onLocationReceived(coordinate)

}