package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.PaymentEntity
import com.kg.gettransfer.data.model.PaymentRequestEntity

interface PaymentRemote {
    suspend fun createPayment(paymentRequest: PaymentRequestEntity): PaymentEntity
}
