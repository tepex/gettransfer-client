package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.socket.TransferDataStoreReceiver
import com.kg.gettransfer.data.model.PointEntity
import com.kg.gettransfer.data.repository.TransferRepositoryImpl
import com.kg.gettransfer.data.socket.TransferEventEmitter
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class TransferDataStoreIO(private val emitter: TransferEventEmitter) :
        TransferEventEmitter,
        TransferDataStoreReceiver,
        KoinComponent {

    private val repository: TransferRepositoryImpl by inject()

    override fun initLocationReceiving() = emitter.initLocationReceiving()

    override fun sendClientLocation(point: PointEntity) = emitter.sendClientLocation(point)

    override fun onLocationReceived(point: PointEntity){}

}