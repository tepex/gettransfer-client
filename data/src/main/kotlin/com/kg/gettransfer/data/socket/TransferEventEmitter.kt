package com.kg.gettransfer.data.socket

import com.kg.gettransfer.data.model.PointEntity

interface TransferEventEmitter {
    fun initLocationReceiving()
    fun sendClientLocation(point: PointEntity)
}