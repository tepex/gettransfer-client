package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.model.OfferEntity
import com.kg.gettransfer.data.repository.OfferRepositoryImpl
import com.kg.gettransfer.data.socket.OfferDataStoreReceiver
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import org.koin.standalone.inject

class OfferDataStoreIO(private val repository: OfferRepositoryImpl): OfferDataStoreReceiver, KoinComponent {
  //  private val repository: OfferRepositoryImpl by inject()

    override fun onNewOffer(offer: OfferEntity) { repository.onNewOfferEvent(offer) }
}