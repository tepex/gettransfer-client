package com.kg.gettransfer.domain.eventListeners

import com.kg.gettransfer.domain.model.Coordinate

interface TransferEventListener {
    fun onLocationReceived(coordinate: Coordinate)
}