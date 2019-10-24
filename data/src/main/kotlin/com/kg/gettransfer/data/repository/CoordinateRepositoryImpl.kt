package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.ds.io.CoordinateSocketDataStoreOutput
import com.kg.gettransfer.data.model.CoordinateEntity
import com.kg.gettransfer.data.model.map
import com.kg.gettransfer.domain.eventListeners.CoordinateEventListener
import com.kg.gettransfer.domain.repository.CoordinateRepository
import org.koin.core.KoinComponent

class CoordinateRepositoryImpl(
    private val socketDataStore: CoordinateSocketDataStoreOutput
) : CoordinateRepository, KoinComponent {

    private val coordinateListeners = mutableSetOf<CoordinateEventListener>()

    fun onCoordinateReceived(coordinateEntity: CoordinateEntity) {
        coordinateListeners.forEach { it.onLocationReceived(coordinateEntity.map()) }
    }

    override fun initCoordinateReceiving(transferId: Long) = socketDataStore.initLocationReceiving(transferId)

    override fun addCoordinateListener(listener: CoordinateEventListener) {
        coordinateListeners.add(listener)
    }

    override fun removeSocketListener(listener: CoordinateEventListener) {
        coordinateListeners.remove(listener)
    }
}
