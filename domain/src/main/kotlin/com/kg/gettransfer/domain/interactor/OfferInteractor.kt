package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.eventListeners.OfferEventListener
import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Result

import com.kg.gettransfer.domain.repository.OfferRepository

class OfferInteractor(private val repository: OfferRepository) {

    var eventReceiver: OfferEventListener? = null

    suspend fun getOffers(transferId: Long, fromCache: Boolean = false) =
        if (fromCache) repository.getOffersCached(transferId) else repository.getOffers(transferId)

    fun clearOffersCache(): Result<Unit> {
        repository.clearOffersCache()
        return Result(Unit)
    }

    fun newOffer(offer: Offer) = repository.newOffer(offer)

    fun onNewOfferEvent(offer: Offer) = eventReceiver?.onNewOfferEvent(offer)
}
