package com.kg.gettransfer.domain.eventListeners

import com.kg.gettransfer.domain.model.Coordinate

interface CoordinateEventListener {
    fun onLocationReceived(coordinate: Coordinate)
}
