package com.kg.gettransfer.presentation.model

data class PaymentRequestModel(
    val transferId: Long,
    val offerId: Long?,
    val bookNowTransportType: String?,
    val gatewayId: String
) {

    companion object {
        const val CARD        = "card"
        const val PLATRON     = "platron"
        const val CHECKOUTCOM = "checkoutcom"

        const val PAYPAL      = "braintree"
        const val GOOGLE_PAY  = "googlepay"
        const val GROUND      = "ground"
    }
}
