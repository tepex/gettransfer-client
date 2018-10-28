package com.kg.gettransfer.remote.model

interface OfferModelListener {
    fun onNewOffer(offer: OfferModel)
    fun onError(e: Exception)
}
