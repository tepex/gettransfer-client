package com.kg.gettransfer.data.model

import com.kg.gettransfer.data.RemoteException

interface OfferEntityListener {
    fun onNewOffer(offer: OfferEntity)
    fun onError(e: RemoteException)
}
