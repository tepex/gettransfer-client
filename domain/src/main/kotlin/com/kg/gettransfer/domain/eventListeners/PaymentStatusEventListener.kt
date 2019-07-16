package com.kg.gettransfer.domain.eventListeners

interface PaymentStatusEventListener {
    fun onNewPaymentStatusEvent(isSuccess: Boolean)
}
