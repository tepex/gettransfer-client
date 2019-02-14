package com.kg.gettransfer.remote.socket

import com.kg.gettransfer.data.model.CoordinateEntity
import com.kg.gettransfer.data.model.OwnCoordinate
import com.kg.gettransfer.data.socket.TransferDataStoreReceiver
import com.kg.gettransfer.remote.socket.emitters.TransferSocketEmitter
import com.kg.gettransfer.remote.socket.emitters.TransferSocketEmitter.Companion.INIT_LOCATION_EVENTS
import com.kg.gettransfer.remote.socket.emitters.TransferSocketEmitter.Companion.OWN_LOCATION
import kotlinx.serialization.json.JSON
import org.koin.core.parameter.parametersOf
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import org.koin.standalone.inject
import org.slf4j.Logger

class TransferSocketImpl: TransferSocketEmitter, KoinComponent {
    private val eventReceiver: TransferDataStoreReceiver by inject()
    private val socketManager: SocketManager = get()
    private val log: Logger by inject { parametersOf("GTR-socket") }

    override fun initLocationReceiving(transferId: Long) = socketManager.emitAck(INIT_LOCATION_EVENTS, arrayOf(transferId))

//    override fun sendOwnLocation(coordinate: CoordinateEntity) = socketManager.emitEvent(OWN_LOCATION, arrayOf(coordinate))
    override fun sendOwnLocation(coordinate: CoordinateEntity) {
    val own = OwnCoordinate(coordinate.lat, coordinate.lon)
    val json = JSON.nonstrict.stringify(OwnCoordinate.serializer(), own)
    log.info("hohoho" + json)
    socketManager.emitAck(OWN_LOCATION, arrayOf(json))
}

    internal fun onLocationUpdated(coordinate: CoordinateEntity) = eventReceiver.onLocationReceived(coordinate)

}