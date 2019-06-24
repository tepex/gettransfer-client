package com.kg.gettransfer.remote.socket.emitters

import com.kg.gettransfer.data.socket.CoordinateEventEmitter

interface CoordinateSocketEmitter : CoordinateEventEmitter {

    companion object {
        const val INIT_LOCATION_EVENTS = "get-carrier-position"
        const val OWN_LOCATION         = "carrier-position"
    }
}
