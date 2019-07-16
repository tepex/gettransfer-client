package com.kg.gettransfer.remote.socket

import com.kg.gettransfer.data.socket.PaymentDataStoreReceiver
import org.koin.core.KoinComponent
import org.koin.core.inject

class PaymentSocketEventer : KoinComponent {

    private val eventReceiver: PaymentDataStoreReceiver by inject()

    internal fun onNewPaymentStatusEvent(isSuccess: Boolean) = eventReceiver.onNewPaymentStatus(isSuccess)
}
