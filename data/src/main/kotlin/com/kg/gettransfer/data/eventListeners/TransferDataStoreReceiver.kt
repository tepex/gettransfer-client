package com.kg.gettransfer.data.eventListeners

import com.kg.gettransfer.data.model.PointEntity

interface TransferDataStoreReceiver {
    fun onLocationReceived(point: PointEntity)
}