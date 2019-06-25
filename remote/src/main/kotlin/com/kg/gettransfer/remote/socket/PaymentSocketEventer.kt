package com.kg.gettransfer.remote.socket

import com.kg.gettransfer.data.socket.PaymentDataStoreReceiver
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class PaymentSocketEventer : KoinComponent {

    private val eventReceiver: PaymentDataStoreReceiver by inject()

    internal fun onNewPaymentStatusEvent(isSuccess: Boolean) = eventReceiver.onNewPaymentStatus(isSuccess)
}
