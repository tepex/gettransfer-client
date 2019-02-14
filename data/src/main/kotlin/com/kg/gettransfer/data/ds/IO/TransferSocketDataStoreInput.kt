package com.kg.gettransfer.data.ds.IO

import com.kg.gettransfer.data.model.CoordinateEntity
import com.kg.gettransfer.data.repository.TransferRepositoryImpl
import com.kg.gettransfer.data.socket.TransferDataStoreReceiver
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class TransferSocketDataStoreInput: KoinComponent, TransferDataStoreReceiver {
    private val repository: TransferRepositoryImpl by inject()

    override fun onLocationReceived(coordinate: CoordinateEntity) { repository.onCoordinateReceived(coordinate) }
}