package com.kg.gettransfer.domain.eventListeners

import com.kg.gettransfer.domain.model.Offer

interface OfferEventListener {
    fun onNewOfferEvent(offer: Offer)
}