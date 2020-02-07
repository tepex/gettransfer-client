package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.BookNowOffer
import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.OfferItem

sealed class OfferType {
    data class OfferId(val offerId: Long) : OfferType()
    data class TransportTypeId(val transportTypeId: String) : OfferType()

    fun getOfferId(): Long? = if (this is OfferId) offerId else null
    fun getTransportTypeName(): String? = if (this is TransportTypeId) transportTypeId else null
}

fun OfferItem.map(): OfferType = when (this) {
    is Offer        -> OfferType.OfferId(id)
    is BookNowOffer -> OfferType.TransportTypeId(transportType.id.toString())
}
