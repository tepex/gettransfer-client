package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.OfferEntity
import org.koin.core.KoinComponent

interface OfferCache : KoinComponent {

    fun setOffer(offer: OfferEntity)

    fun setOffers(transferId: Long, offers: List<OfferEntity>)

    fun getOffers(id: Long): List<OfferEntity>

    fun deleteAllOffers()
}
