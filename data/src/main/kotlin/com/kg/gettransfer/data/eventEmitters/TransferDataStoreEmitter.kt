package com.kg.gettransfer.data.eventEmitters

import com.kg.gettransfer.data.model.PointEntity

interface TransferDataStoreEmitter {
    fun initLocationReceiving()
    fun sendClientLocation(point: PointEntity)
}