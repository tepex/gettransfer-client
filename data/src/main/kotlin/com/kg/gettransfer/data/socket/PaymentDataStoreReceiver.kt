package com.kg.gettransfer.data.socket

interface PaymentDataStoreReceiver {
    fun onNewPaymentStatus(isSuccess: Boolean)
}