package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.eventListeners.OfferEventListener
import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Result

import com.kg.gettransfer.domain.repository.OfferRepository

class OfferInteractor(private val repository: OfferRepository) {

    var offers: List<Offer> = emptyList()
    var lastTransferId = -1L
    var eventReceiver: OfferEventListener? = null

    var offerViewExpanded: Boolean
    get() = repository.offerViewExpanded
    set(value) {
        repository.offerViewExpanded = value
    }

    suspend fun getOffers(transferId: Long, fromCache: Boolean = false): Result<List<Offer>> {
        val result = if (fromCache) repository.getOffersCached(transferId) else repository.getOffers(transferId)
        offers = result.model
        return result
    }

    fun getOffer(id: Long) = offers.find { it.id == id }

    fun newOffer(offer: Offer): Result<Offer> {
        val newOffer = repository.newOffer(offer)
        offers = offers.toMutableList().apply { add(newOffer.model) }
        return newOffer
    }

    fun clearOffersCache(): Result<Unit> {
        repository.clearOffersCache()
        return Result(Unit)
    }

    fun onNewOfferEvent(offer: Offer) = eventReceiver?.onNewOfferEvent(offer)
}
