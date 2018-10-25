package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.OfferEntity

interface OfferSocket {
    nd fun getOffers(id: Long): List<OfferEntity>
}
