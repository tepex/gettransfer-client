package com.kg.gettransfer.data.ds.io

import com.kg.gettransfer.data.model.CoordinateEntity
import com.kg.gettransfer.data.repository.CoordinateRepositoryImpl
import com.kg.gettransfer.data.socket.CoordinateDataStoreReceiver
import org.koin.core.KoinComponent
import org.koin.core.inject

class CoordinateSocketDataStoreInput : KoinComponent, CoordinateDataStoreReceiver {

    private val repository: CoordinateRepositoryImpl by inject()

    override fun onLocationReceived(coordinate: CoordinateEntity) { repository.onCoordinateReceived(coordinate) }
}
