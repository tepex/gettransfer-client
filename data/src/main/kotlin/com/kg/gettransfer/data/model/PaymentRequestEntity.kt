package com.kg.gettransfer.data.model

data class PaymentRequestEntity(
    val transferId: Long,
    val offerId: Long?,
    val gatewayId: String,
    val percentage: Int
) {

    companion object {
        const val TRANSFER_ID = "transfer_id"
        const val OFFER_ID    = "offer_id"
        const val GATEWAY_ID  = "gateway_id"
        const val PERCENTAGE  = "percentage"
    }
}
