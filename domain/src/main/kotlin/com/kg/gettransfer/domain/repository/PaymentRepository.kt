package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Payment
import com.kg.gettransfer.domain.model.PaymentRequest
import com.kg.gettransfer.domain.model.PaymentStatus
import com.kg.gettransfer.domain.model.PaymentStatusRequest

interface PaymentRepository {
    suspend fun getPayment(paymentRequest: PaymentRequest): Payment
    suspend fun changeStatusPayment(paymentStatusRequest: PaymentStatusRequest): PaymentStatus
}
