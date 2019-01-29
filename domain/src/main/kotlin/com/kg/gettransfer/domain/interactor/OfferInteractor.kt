package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.eventListeners.OfferEventListener
import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Result

import com.kg.gettransfer.domain.repository.OfferRepository

class OfferInteractor(private val repository: OfferRepository) {

    var offers: List<Offer> = emptyList()
    var lastTransferId = -1L
    var eventReceiver: OfferEventListener? = null

    suspend fun getOffers(transferId: Long, fromCache: Boolean = false) =
            when(fromCache){
                false -> repository.getOffers(transferId)
                true -> repository.getOffersCached(transferId)
            }.also { offers = it .model }
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
