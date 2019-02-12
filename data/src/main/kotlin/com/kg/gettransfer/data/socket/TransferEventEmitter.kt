package com.kg.gettransfer.data.socket

import com.kg.gettransfer.data.model.CoordinateEntity

interface TransferEventEmitter {
    fun initLocationReceiving(transferId: Long)
    fun sendOwnLocation(coordinate: CoordinateEntity)
}