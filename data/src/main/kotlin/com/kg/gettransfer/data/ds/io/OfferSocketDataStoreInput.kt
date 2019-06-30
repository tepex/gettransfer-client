package com.kg.gettransfer.data.ds.io

import com.kg.gettransfer.data.model.OfferEntity
import com.kg.gettransfer.data.repository.OfferRepositoryImpl
import com.kg.gettransfer.data.socket.OfferDataStoreReceiver
import org.koin.core.KoinComponent
import org.koin.core.inject

class OfferSocketDataStoreInput : OfferDataStoreReceiver, KoinComponent {

    private val repository: OfferRepositoryImpl by inject()

    override fun onNewOffer(offer: OfferEntity) { repository.onNewOfferEvent(offer) }
}
