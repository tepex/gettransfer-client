package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.eventListeners.CoordinateEventListener
import com.kg.gettransfer.domain.model.Coordinate
import com.kg.gettransfer.domain.repository.CoordinateRepository

class CoordinateInteractor(private val repository: CoordinateRepository) {
    var coordinateEventListener: CoordinateEventListener? = null

    fun onCoordinateReceived(coordinate: Coordinate) = coordinateEventListener?.onLocationReceived(coordinate)
    fun initCoordinatesReceiving(transferId: Long) = repository.initCoordinateReceiving(transferId)
    fun sendOwnCoordinates(coordinate: Coordinate) = repository.sendOwnCoordinate(coordinate)
}
