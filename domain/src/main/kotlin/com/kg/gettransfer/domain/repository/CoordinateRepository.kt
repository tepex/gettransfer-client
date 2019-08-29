package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.eventListeners.CoordinateEventListener

interface CoordinateRepository {
    fun initCoordinateReceiving(transferId: Long)

    fun addCoordinateListener(listener: CoordinateEventListener)
    fun removeSocketListener(listener: CoordinateEventListener)
}
