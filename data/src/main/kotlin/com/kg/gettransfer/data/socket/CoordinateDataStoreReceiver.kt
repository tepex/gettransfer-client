package com.kg.gettransfer.data.socket

import com.kg.gettransfer.data.model.CoordinateEntity

interface CoordinateDataStoreReceiver {

    fun onLocationReceived(coordinate: CoordinateEntity)
}
