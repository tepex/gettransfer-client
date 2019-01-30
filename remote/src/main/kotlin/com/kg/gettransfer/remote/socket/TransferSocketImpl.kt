package com.kg.gettransfer.remote.socket

import com.kg.gettransfer.data.socket.TransferDataStoreReceiver
import com.kg.gettransfer.data.model.PointEntity
import com.kg.gettransfer.remote.socket.emitters.TransferSocketEmitter
import com.kg.gettransfer.remote.socket.emitters.TransferSocketEmitter.Companion.INIT_LOCATION_EVENTS
import org.koin.standalone.KoinComponent
import org.koin.standalone.get

class TransferSocketImpl: TransferSocketEmitter, KoinComponent {
    private val eventReceiver = get<TransferDataStoreReceiver>()
    private val socketManager: SocketManager = get()

    override fun initLocationReceiving() {}

    override fun sendClientLocation(point: PointEntity) = socketManager.emitEvent(INIT_LOCATION_EVENTS, emptyArray<Any>())

    internal fun onLocationUpdated(string: String) = eventReceiver.onLocationReceived(PointEntity(0f, 0f))

}