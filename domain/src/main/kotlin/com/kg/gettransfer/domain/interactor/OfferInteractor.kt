package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.OfferListener

import com.kg.gettransfer.domain.model.Offer

import com.kg.gettransfer.domain.repository.OfferRepository

class OfferInteractor(private val repository: OfferRepository) {
    var transferId: Long? = null
    var selectedOfferId: Long? = null
    lateinit var offers: List<Offer>
    
    suspend fun getOffers(transferId: Long): List<Offer> {
       offers = repository.getOffers(transferId)
       return offers
    }
    
    fun getOffer(id: Long) = offers.find { it.id == id }
    
    fun setListener(listener: OfferListener)    { repository.setListener(listener) }
    fun removeListener(listener: OfferListener) { repository.removeListener(listener) }
}
