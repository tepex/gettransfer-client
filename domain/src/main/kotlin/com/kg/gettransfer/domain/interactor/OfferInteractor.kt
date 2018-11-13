package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.Offer

import com.kg.gettransfer.domain.repository.OfferRepository

class OfferInteractor(private val repository: OfferRepository) {
    var transferId: Long? = null
    var selectedOfferId: Long? = null
    lateinit var offers: List<Offer>


    suspend fun getOffers(transferId: Long): List<Offer> {
        offers = repository.getOffers(transferId)
        this.transferId = transferId
        return offers
    }
    
    fun getOffer(id: Long) = offers.find { it.id == id }
    
    fun newOffer(offer: Offer) { repository.newOffer(offer) }
}
