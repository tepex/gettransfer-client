package com.kg.gettransfer.domain.eventListeners

import com.kg.gettransfer.domain.model.Point

interface TransferEventListener {
    fun onLocationReceived(point: Point)
}