package com.kg.gettransfer.remote.socket

import com.kg.gettransfer.data.model.OfferEntity
import com.kg.gettransfer.data.socket.OfferDataStoreReceiver
import org.koin.core.KoinComponent
import org.koin.core.inject

class OfferSocketImpl(private val socket: SocketManager) : KoinComponent {

    private val receiver: OfferDataStoreReceiver by inject()

    internal fun onNewOffer(offer: OfferEntity) = receiver.onNewOffer(offer)
}
