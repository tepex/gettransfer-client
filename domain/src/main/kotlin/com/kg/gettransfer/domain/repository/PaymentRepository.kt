package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Payment
import com.kg.gettransfer.domain.model.PaymentRequest
import com.kg.gettransfer.domain.model.PaymentStatus
import com.kg.gettransfer.domain.model.PaymentStatusRequest
import com.kg.gettransfer.domain.model.Result

interface PaymentRepository {
    suspend fun getPayment(paymentRequest: PaymentRequest): Result<Payment>
    suspend fun changeStatusPayment(paymentStatusRequest: PaymentStatusRequest): Result<PaymentStatus>
    suspend fun getBrainTreeToken(): Result<BraintreeToken>
    suspend fun confirmPaypal(paymentId: Long, nonce: String): Result<PaymentStatus>
}
