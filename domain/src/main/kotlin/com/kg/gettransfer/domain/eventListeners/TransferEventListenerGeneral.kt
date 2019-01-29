package com.kg.gettransfer.domain.eventListeners

interface TransferEventListenerGeneral {
    fun <P> onLocationReceived(point: P)
}