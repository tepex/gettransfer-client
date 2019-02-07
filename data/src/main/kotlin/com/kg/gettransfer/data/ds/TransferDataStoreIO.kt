package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.socket.TransferDataStoreReceiver
import com.kg.gettransfer.data.model.CoordinateEntity
import com.kg.gettransfer.data.repository.TransferRepositoryImpl
import com.kg.gettransfer.data.socket.TransferEventEmitter
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class TransferDataStoreIO(private val emitter: TransferEventEmitter) :
        TransferDataStoreReceiver,
        KoinComponent {

    private val repository: TransferRepositoryImpl by inject()

    fun initLocationReceiving() = emitter.initLocationReceiving()

    fun sendOwnLocation(coordinate: CoordinateEntity) = emitter.sendOwnLocation(coordinate)

    override fun onLocationReceived(coordinate: CoordinateEntity) { repository.onCoordinateReceived(coordinate) }

}