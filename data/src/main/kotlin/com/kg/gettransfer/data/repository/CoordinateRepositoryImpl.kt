package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.ds.IO.CoordinateSocketDataStoreOutput
import com.kg.gettransfer.data.mapper.CoordinateMapper
import com.kg.gettransfer.data.model.CoordinateEntity
import com.kg.gettransfer.domain.interactor.CoordinateInteractor
import com.kg.gettransfer.domain.model.Coordinate
import com.kg.gettransfer.domain.repository.CoordinateRepository
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import org.koin.standalone.inject

class CoordinateRepositoryImpl(private val socketDataStore: CoordinateSocketDataStoreOutput):
        CoordinateRepository, KoinComponent {
    private val coordinateInteractor: CoordinateInteractor by inject()
    private val coordinateMapper: CoordinateMapper = get()

    fun onCoordinateReceived(coordinateEntity: CoordinateEntity) =
            coordinateInteractor.onCoordinateReceived(coordinateMapper.fromEntity(coordinateEntity))

    override fun sendOwnCoordinate(coordinate: Coordinate) {
        socketDataStore.sendOwnLocation(coordinateMapper.toEntity(coordinate))
    }

    override fun initCoordinateReceiving(transferId: Long) = socketDataStore.initLocationReceiving(transferId)
}