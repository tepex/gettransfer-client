package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.eventListeners.TransferDataStoreReceiver
import com.kg.gettransfer.data.model.PointEntity
import com.kg.gettransfer.data.repository.TransferRepositoryImpl
import com.kg.gettransfer.data.eventEmitters.TransferDataStoreEmitter
import com.kg.gettransfer.domain.eventEmitters.TransferEmitterGeneral
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class TransferDataStoreIO(private val emitter: TransferDataStoreEmitter) :
        TransferDataStoreEmitter,
        TransferDataStoreReceiver,
        KoinComponent {

    private val repository: TransferRepositoryImpl by inject()

    override fun initLocationReceiving() = emitter.initLocationReceiving()

    override fun sendClientLocation(point: PointEntity) = emitter.sendClientLocation(point)

    override fun onLocationReceived(point: PointEntity){}

}