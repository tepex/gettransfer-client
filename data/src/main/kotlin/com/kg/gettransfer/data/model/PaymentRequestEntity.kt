package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.PaymentRequest

data class PaymentRequestEntity(
    val transferId: Long,
    val offerType: OfferType,
    val gatewayId: String
) {

    companion object {
        const val TRANSFER_ID = "transfer_id"
        const val OFFER_ID    = "offer_id"
        const val BOOK_NOW_TRANSPORT_TYPE  = "book_now_transport_type"
        const val GATEWAY_ID  = "gateway_id"
    }
}

fun PaymentRequest.map() = PaymentRequestEntity(transferId, offerItem.map(), gateway.toString())
