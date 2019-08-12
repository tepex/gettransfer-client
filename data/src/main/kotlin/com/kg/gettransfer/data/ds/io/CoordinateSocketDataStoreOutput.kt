package com.kg.gettransfer.data.ds.io

import com.kg.gettransfer.data.model.CoordinateEntity
import com.kg.gettransfer.data.socket.CoordinateEventEmitter
import org.koin.core.KoinComponent

class CoordinateSocketDataStoreOutput(private val emitter: CoordinateEventEmitter) : KoinComponent {

    fun initLocationReceiving() = emitter.initLocationReceiving()

    fun sendOwnLocation(coordinate: CoordinateEntity) = emitter.sendOwnLocation(coordinate)
}
