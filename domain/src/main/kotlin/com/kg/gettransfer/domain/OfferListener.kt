package com.kg.gettransfer.domain

import com.kg.gettransfer.domain.model.Offer

interface OfferListener {
    fun onNewOffer(offer: Offer)
    fun onError(e: Exception)
}
