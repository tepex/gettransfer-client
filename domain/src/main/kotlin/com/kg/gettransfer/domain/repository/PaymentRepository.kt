package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Payment
import com.kg.gettransfer.domain.model.PaymentRequest

interface PaymentRepository {
    suspend fun getPayment(paymentRequest: PaymentRequest): Payment
    suspend fun changeStatusPayment(paymentId: Long, pgOrderId: Long, withoutRedirect: Boolean, status: String)
}
