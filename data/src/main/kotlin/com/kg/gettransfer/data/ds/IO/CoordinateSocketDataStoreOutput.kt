package com.kg.gettransfer.data.ds.IO

import com.kg.gettransfer.data.model.CoordinateEntity
import com.kg.gettransfer.data.socket.CoordinateEventEmitter
import org.koin.standalone.KoinComponent

class CoordinateSocketDataStoreOutput(private val emitter: CoordinateEventEmitter) : KoinComponent {

    fun initLocationReceiving(transferId: Long) = emitter.initLocationReceiving(transferId)
    fun sendOwnLocation(coordinate: CoordinateEntity) = emitter.sendOwnLocation(coordinate)
}