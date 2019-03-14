package com.kg.gettransfer.data.socket

import com.kg.gettransfer.data.model.OfferEntity

interface OfferDataStoreReceiver {
    fun onNewOffer(offer: OfferEntity)
}