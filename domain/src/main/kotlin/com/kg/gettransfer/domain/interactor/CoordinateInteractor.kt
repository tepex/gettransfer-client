package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.eventListeners.CoordinateEventListener
import com.kg.gettransfer.domain.model.Coordinate
import com.kg.gettransfer.domain.repository.CoordinateRepository

class CoordinateInteractor(private val repository: CoordinateRepository) {
    fun initCoordinatesReceiving() = repository.initCoordinateReceiving()
    fun sendOwnCoordinates(coordinate: Coordinate) = repository.sendOwnCoordinate(coordinate)

    fun addCoordinateListener(listener: CoordinateEventListener)    { repository.addCoordinateListener(listener) }
    fun removeCoordinateListener(listener: CoordinateEventListener) { repository.removeSocketListener(listener) }
}
