package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Result

import com.kg.gettransfer.domain.repository.OfferRepository

class OfferInteractor(private val repository: OfferRepository) {
    var offers: List<Offer> = emptyList()
    var lastTransferId = -1L

    suspend fun getOffers(transferId: Long) = repository.getOffers(transferId).also { offers = it .model }
    fun getOffer(id: Long) = offers.find { it.id == id }

    fun newOffer(offer: Offer) {
        val newOffer = repository.newOffer(offer)
        offers = offers.toMutableList().apply { add(newOffer.model) }
    }

    fun clearOffersCache(): Result<Unit> {
        repository.clearOffersCache()
        return Result(Unit)
    }
}
