package com.kg.gettransfer.presentation.view

interface PaypalConnectionView : BaseView {
    fun stopAnimation()

    companion object {
        val EXTRA_TRANSFER_ID = "${PaypalConnectionView::class.java.name}.transferId"
        val EXTRA_OFFER_ID    = "${PaypalConnectionView::class.java.name}.offerId"
        val EXTRA_PAYMENT_ID  = "${PaypalConnectionView::class.java.name}.paymentId"
        val EXTRA_NONCE       = "${PaypalConnectionView::class.java.name}.nonce"
    }
}
