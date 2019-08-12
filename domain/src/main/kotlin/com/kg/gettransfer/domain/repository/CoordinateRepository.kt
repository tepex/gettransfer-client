package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.eventListeners.CoordinateEventListener
import com.kg.gettransfer.domain.model.Coordinate

interface CoordinateRepository {
    fun sendOwnCoordinate(coordinate: Coordinate)
    fun initCoordinateReceiving()

    fun addCoordinateListener(listener: CoordinateEventListener)
    fun removeSocketListener(listener: CoordinateEventListener)
}
