package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.OfferListener

interface OfferRepository {
    suspend fun getOffers(id: Long): List<Offer>
    fun setListener(listener: OfferListener)
    fun removeListener(listener: OfferListener)
}
