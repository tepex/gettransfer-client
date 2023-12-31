package com.kg.gettransfer.data.ds.io

import com.kg.gettransfer.data.socket.CoordinateEventEmitter
import org.koin.core.KoinComponent

class CoordinateSocketDataStoreOutput(private val emitter: CoordinateEventEmitter) : KoinComponent {

    fun initLocationReceiving(transferId: Long) = emitter.initLocationReceiving(transferId)
}
