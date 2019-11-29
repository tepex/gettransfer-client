package com.kg.gettransfer.presentation.view

interface PaymentErrorView : BaseView {

    companion object {
        val EXTRA_TRANSFER_ID = "${PaymentErrorView::class.java.name}.transferId"
        val EXTRA_GATEWAY_ID  = "${PaymentErrorView::class.java.name}.gatewayId"
    }
}
