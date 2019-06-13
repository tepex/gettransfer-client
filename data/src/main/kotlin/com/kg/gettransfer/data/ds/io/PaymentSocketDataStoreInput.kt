package com.kg.gettransfer.data.ds.io

import com.kg.gettransfer.data.repository.PaymentRepositoryImpl
import com.kg.gettransfer.data.socket.PaymentDataStoreReceiver
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class PaymentSocketDataStoreInput: PaymentDataStoreReceiver, KoinComponent {
    private val repository: PaymentRepositoryImpl by inject()

    override fun onNewPaymentStatus(isSuccess: Boolean) { repository.onNewPaymentStatusEvent(isSuccess) }
}