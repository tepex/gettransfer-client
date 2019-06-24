package com.kg.gettransfer.data.socket

import com.kg.gettransfer.data.model.OfferEntity

interface OfferEventEmitter {

    fun onNewOffer(): OfferEntity
}
