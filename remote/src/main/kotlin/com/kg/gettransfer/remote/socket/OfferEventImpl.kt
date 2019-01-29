package com.kg.gettransfer.remote.socket

import com.kg.gettransfer.data.model.OfferEntity
import com.kg.gettransfer.data.socket.OfferDataStoreReceiver
import com.kg.gettransfer.remote.mapper.OfferMapper
import com.kg.gettransfer.remote.model.OfferModel
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import org.koin.standalone.inject

class OfferEventImpl(private val socket: SocketManager): KoinComponent {
    private val receiver: OfferDataStoreReceiver by inject()

    internal fun onNewOffer(offer: OfferEntity) = receiver.onNewOffer(offer)
}