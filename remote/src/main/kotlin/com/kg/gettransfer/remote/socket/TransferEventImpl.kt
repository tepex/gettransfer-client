package com.kg.gettransfer.remote.socket

import com.kg.gettransfer.data.socket.TransferDataStoreReceiver
import com.kg.gettransfer.data.model.PointEntity
import com.kg.gettransfer.remote.socket.emitters.TransferSocketEmitter
import org.koin.standalone.KoinComponent
import org.koin.standalone.get

class TransferEventImpl: TransferSocketEmitter, KoinComponent {
    private val eventReceiver = get<TransferDataStoreReceiver>()
    private val socketManager: SocketManager = get()

    override fun initLocationReceiving() {}

    override fun sendClientLocation(point: PointEntity) {}

    fun onLocationUpdated(string: String) = eventReceiver.onLocationReceived(PointEntity(0f, 0f))

}