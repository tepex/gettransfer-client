package com.kg.gettransfer.data.eventListeners

import com.kg.gettransfer.data.model.OfferEntity

interface OfferEventReceiver {
    fun onNewOffer(offer: OfferEntity)
}