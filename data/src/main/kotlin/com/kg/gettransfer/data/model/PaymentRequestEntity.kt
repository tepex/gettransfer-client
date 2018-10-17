package com.kg.gettransfer.data.model

data class PaymentRequestEntity(val transferId: Long, val offerId: Long?, val gatewayId: String, val percentage: Int)
