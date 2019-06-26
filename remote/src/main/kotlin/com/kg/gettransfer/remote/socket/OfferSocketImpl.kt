package com.kg.gettransfer.remote.socket

import com.kg.gettransfer.data.model.OfferEntity
import com.kg.gettransfer.data.socket.OfferDataStoreReceiver
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class OfferSocketImpl(private val socket: SocketManager) : KoinComponent {

    private val receiver: OfferDataStoreReceiver by inject()

    internal fun onNewOffer(offer: OfferEntity) = receiver.onNewOffer(offer)
}
