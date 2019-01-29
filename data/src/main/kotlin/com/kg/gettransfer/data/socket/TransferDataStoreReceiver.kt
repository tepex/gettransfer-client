package com.kg.gettransfer.data.socket

import com.kg.gettransfer.data.model.PointEntity

interface TransferDataStoreReceiver {
    fun onLocationReceived(point: PointEntity)
}