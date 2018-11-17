package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.OfferEntity

import org.koin.standalone.KoinComponent

interface OfferRemote: KoinComponent {
    suspend fun getOffers(id: Long): List<OfferEntity>
}
