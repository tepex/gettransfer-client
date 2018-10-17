package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.PaymentEntity
import com.kg.gettransfer.data.model.PaymentRequestEntity

interface PaymentDataStore {
    suspend fun createPayment(paymentRequest: PaymentRequestEntity): PaymentEntity
}
