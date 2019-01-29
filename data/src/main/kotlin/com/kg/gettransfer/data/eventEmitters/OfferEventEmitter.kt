package com.kg.gettransfer.data.eventEmitters

import com.kg.gettransfer.data.model.OfferEntity

interface OfferEventEmitter {
    fun onNewOffer(): OfferEntity
}