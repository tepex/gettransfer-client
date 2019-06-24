package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.ds.io.CoordinateSocketDataStoreOutput
import com.kg.gettransfer.data.model.CoordinateEntity
import com.kg.gettransfer.data.model.map
import com.kg.gettransfer.domain.interactor.CoordinateInteractor
import com.kg.gettransfer.domain.model.Coordinate
import com.kg.gettransfer.domain.repository.CoordinateRepository
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class CoordinateRepositoryImpl(
    private val socketDataStore: CoordinateSocketDataStoreOutput
) : CoordinateRepository, KoinComponent {

    private val coordinateInteractor: CoordinateInteractor by inject()

    fun onCoordinateReceived(coordinateEntity: CoordinateEntity) =
        coordinateInteractor.onCoordinateReceived(coordinateEntity.map())

    override fun sendOwnCoordinate(coordinate: Coordinate) {
        socketDataStore.sendOwnLocation(coordinate.map())
    }

    override fun initCoordinateReceiving(transferId: Long) = socketDataStore.initLocationReceiving(transferId)
}
