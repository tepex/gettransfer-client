package com.kg.gettransfer.extensions

import com.kg.gettransfer.domain.model.BookNowOffer
import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.TransportType

fun List<Offer>.getOffer(offerId: Long) = find { it.id == offerId }

fun List<BookNowOffer>.getOffer(transportTypeId: TransportType.ID) = find { it.transportType.id == transportTypeId }