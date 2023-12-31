package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.OfferEntity
import org.koin.core.KoinComponent

interface OfferDataStore : KoinComponent {

    fun setOffers(transferId: Long, offers: List<OfferEntity>)

    fun setOffer(offer: OfferEntity)

    suspend fun getOffers(id: Long): List<OfferEntity>

    fun clearOffersCache()
}
