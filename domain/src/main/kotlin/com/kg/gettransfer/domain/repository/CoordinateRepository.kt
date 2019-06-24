package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Coordinate

interface CoordinateRepository {
    fun sendOwnCoordinate(coordinate: Coordinate)
    fun initCoordinateReceiving(transferId: Long)
}
