package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.OfferEntity

interface OfferSocket {
    fun onNewOffer(offer: OfferEntity)
    fun onError(e: RemoteException)
}
