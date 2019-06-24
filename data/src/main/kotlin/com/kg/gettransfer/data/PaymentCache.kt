package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.PaymentEntity
import com.kg.gettransfer.data.model.PaymentRequestEntity
import com.kg.gettransfer.data.model.PaymentStatusEntity
import com.kg.gettransfer.data.model.PaymentStatusRequestEntity
import org.koin.standalone.KoinComponent

interface PaymentCache : KoinComponent {

    suspend fun createPayment(paymentRequest: PaymentRequestEntity): PaymentEntity

    suspend fun changeStatusPayment(paymentStatusRequest: PaymentStatusRequestEntity): PaymentStatusEntity
}
