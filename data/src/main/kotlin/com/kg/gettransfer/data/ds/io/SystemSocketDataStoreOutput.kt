package com.kg.gettransfer.data.ds.io

import com.kg.gettransfer.data.socket.SystemEventEmitter

class SystemSocketDataStoreOutput(private val emitter: SystemEventEmitter) {

    fun connectSocket() = emitter.connectSocket()

    fun disconnectSocket() = emitter.disconnectSocket()
}
