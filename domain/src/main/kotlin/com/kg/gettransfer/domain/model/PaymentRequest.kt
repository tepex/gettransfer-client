package com.kg.gettransfer.domain.model

import java.util.Locale

data class PaymentRequest(
    val transferId: Long,
    val offerItem: OfferItem,
    val gateway: Gateway
) {
    enum class Gateway {
        CARD, PLATRON, CHECKOUTCOM, BRAINTREE, GOOGLEPAY, GROUND;

        override fun toString() = name.toLowerCase(Locale.US)
    }
}
