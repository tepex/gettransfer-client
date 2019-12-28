package com.kg.gettransfer.domain.model

data class PaymentRequest(
    val transferId: Long,
    val offerId: Long?,
    val gatewayId: String,
    val bookNowTransportType: String?
)
