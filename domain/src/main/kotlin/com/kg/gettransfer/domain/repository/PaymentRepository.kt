package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.*

interface PaymentRepository {
    suspend fun getPayment(paymentRequest: PaymentRequest): Result<Payment>
    suspend fun changeStatusPayment(paymentStatusRequest: PaymentStatusRequest): Result<PaymentStatus>
    suspend fun getBrainTreeToken(): Result<BraintreeToken>
    suspend fun confirmPaypal(paymentId: Long, nonce: String): Result<PaymentStatus>
}
