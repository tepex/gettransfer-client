package com.kg.gettransfer.domain.model

import com.kg.gettransfer.domain.ApiException

interface OfferListener {
    fun onNewOffer(offer: Offer)
    fun onError(e: ApiException)
}
