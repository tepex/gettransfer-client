package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.*

import org.koin.standalone.KoinComponent

interface PaymentRemote: KoinComponent {
    suspend fun createPayment(paymentRequest: PaymentRequestEntity): PaymentEntity
    suspend fun changeStatusPayment(paymentStatusRequest: PaymentStatusRequestEntity): PaymentStatusEntity
    suspend fun getBraintreeToken(): BraintreeTokenEntity
    suspend fun confirmPaypal(paymentId: Long, nonce: String): PaymentStatusEntity
}
