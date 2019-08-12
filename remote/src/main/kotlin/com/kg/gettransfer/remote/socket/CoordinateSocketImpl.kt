package com.kg.gettransfer.remote.socket

import com.kg.gettransfer.data.model.CoordinateEntity
import com.kg.gettransfer.data.socket.CoordinateDataStoreReceiver
import com.kg.gettransfer.remote.model.OwnCoordinate
import com.kg.gettransfer.remote.socket.emitters.CoordinateSocketEmitter
import com.kg.gettransfer.remote.socket.emitters.CoordinateSocketEmitter.Companion.INIT_LOCATION_EVENTS
import com.kg.gettransfer.remote.socket.emitters.CoordinateSocketEmitter.Companion.OWN_LOCATION
import kotlinx.serialization.json.JSON
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject

class CoordinateSocketImpl : CoordinateSocketEmitter, KoinComponent {

    private val eventReceiver: CoordinateDataStoreReceiver by inject()
    private val socketManager: SocketManager = get()

    override fun initLocationReceiving() {
        socketManager.emitEvent(INIT_LOCATION_EVENTS, null)
        //socketManager.emitAck(INIT_LOCATION_EVENTS, arrayOf(transferId))
    }

    override fun sendOwnLocation(coordinate: CoordinateEntity) {
        val json = JSON.nonstrict.stringify(OwnCoordinate.serializer(), OwnCoordinate(coordinate.lat, coordinate.lon))
        socketManager.emitAck(OWN_LOCATION, arrayOf(json))
    }

    internal fun onLocationUpdated(coordinate: CoordinateEntity) = eventReceiver.onLocationReceived(coordinate)
}
