package com.kg.gettransfer.presentation.model

data class PaymentRequestModel(
    val transferId: Long,
    val offerId: Long?,
    val bookNowTransportType: String?
) {

    var gatewayId = PLATRON
    var percentage = FULL_PRICE

    companion object {
        const val FULL_PRICE = 100
        const val PRICE_30   = 30

        const val PLATRON = "platron"
        const val PAYPAL  = "braintree"
        const val GROUND  = "ground"
    }
}
