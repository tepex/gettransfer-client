package com.kg.gettransfer.presentation.model

data class PaymentRequestModel(val transferId: Long, val offerId: Long?,
                               val bookNowTransportType: String?) {
    companion object {
        const val FULL_PRICE = 100
        const val PRICE_30   = 30
        
        const val PLATRON     = "platron"
        const val PAYPAL      = "paypal"
        const val PAYMENTWALL = "paymentwall"
    }
    
    var gatewayId = PLATRON
    var percentage = FULL_PRICE
}
