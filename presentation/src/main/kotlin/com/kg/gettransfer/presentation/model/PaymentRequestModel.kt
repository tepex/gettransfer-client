package com.kg.gettransfer.presentation.model

data class PaymentRequestModel(
    val transferId: Long,
    val offerId: Long?,
    val bookNowTransportType: String?
) {

    var gatewayId = CHECKOUT

    companion object {
        const val PLATRON    = "platron"
        const val CHECKOUT   = "checkoutcom"
        const val PAYPAL     = "braintree"
        const val GOOGLE_PAY = "googlepay"
        const val GROUND     = "ground"
    }
}
