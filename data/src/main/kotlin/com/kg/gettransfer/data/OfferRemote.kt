package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.OfferEntity
import org.koin.core.KoinComponent

interface OfferRemote : KoinComponent {

    suspend fun getOffers(id: Long): List<OfferEntity>
}
