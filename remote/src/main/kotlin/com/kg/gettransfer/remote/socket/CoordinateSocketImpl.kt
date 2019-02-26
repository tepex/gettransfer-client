package com.kg.gettransfer.remote.socket

import com.kg.gettransfer.data.model.CoordinateEntity
import com.kg.gettransfer.data.model.OwnCoordinate
import com.kg.gettransfer.data.socket.CoordinateDataStoreReceiver
import com.kg.gettransfer.remote.socket.emitters.CoordinateSocketEmitter
import com.kg.gettransfer.remote.socket.emitters.CoordinateSocketEmitter.Companion.INIT_LOCATION_EVENTS
import com.kg.gettransfer.remote.socket.emitters.CoordinateSocketEmitter.Companion.OWN_LOCATION
import kotlinx.serialization.json.JSON
import org.koin.core.parameter.parametersOf
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import org.koin.standalone.inject
import org.slf4j.Logger

class CoordinateSocketImpl: CoordinateSocketEmitter, KoinComponent {
    private val eventReceiver: CoordinateDataStoreReceiver by inject()
    private val socketManager: SocketManager = get()
    private val log: Logger by inject { parametersOf("GTR-socket") }

    override fun initLocationReceiving(transferId: Long) = socketManager.emitAck(INIT_LOCATION_EVENTS, arrayOf(transferId))

//    override fun sendOwnLocation(coordinate: CoordinateEntity) = socketManager.emitEvent(OWN_LOCATION, arrayOf(coordinate))
    override fun sendOwnLocation(coordinate: CoordinateEntity) {
    val own = OwnCoordinate(coordinate.lat, coordinate.lon)
    val json = JSON.nonstrict.stringify(OwnCoordinate.serializer(), own)
    socketManager.emitAck(OWN_LOCATION, arrayOf(json))
}

    internal fun onLocationUpdated(coordinate: CoordinateEntity) = eventReceiver.onLocationReceived(coordinate)

}