package com.kg.gettransfer.domain.eventListeners

interface OfferEventListenerGeneral {
    fun <O> onNewOffer(offer: O)
}