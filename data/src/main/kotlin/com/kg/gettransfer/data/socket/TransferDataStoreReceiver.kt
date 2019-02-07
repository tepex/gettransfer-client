package com.kg.gettransfer.data.socket

import com.kg.gettransfer.data.model.CoordinateEntity

interface TransferDataStoreReceiver {
    fun onLocationReceived(coordinate: CoordinateEntity)
}