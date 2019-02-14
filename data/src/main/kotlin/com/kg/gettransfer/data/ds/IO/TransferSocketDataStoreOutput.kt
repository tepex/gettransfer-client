package com.kg.gettransfer.data.ds.IO

import com.kg.gettransfer.data.socket.TransferDataStoreReceiver
import com.kg.gettransfer.data.model.CoordinateEntity
import com.kg.gettransfer.data.repository.TransferRepositoryImpl
import com.kg.gettransfer.data.socket.TransferEventEmitter
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class TransferSocketDataStoreOutput(private val emitter: TransferEventEmitter) : KoinComponent {

    fun initLocationReceiving(transferId: Long) = emitter.initLocationReceiving(transferId)
    fun sendOwnLocation(coordinate: CoordinateEntity) = emitter.sendOwnLocation(coordinate)
}