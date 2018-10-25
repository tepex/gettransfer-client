package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.OfferEntity

import com.kg.gettransfer.data.model.OfferEntityListener

interface OfferDataStore {
    suspend fun getOffers(id: Long): List<OfferEntity>
    fun setListener(listener: OfferEntityListener)
    fun removeListener(listener: OfferEntityListener)
}
