package com.kg.gettransfer.remote.socket.emitters

import com.kg.gettransfer.data.socket.TransferEventEmitter

interface TransferSocketEmitter : TransferEventEmitter {
    companion object {
        const val INIT_LOCATION_EVENTS = "get-carrier-position"
    }
}