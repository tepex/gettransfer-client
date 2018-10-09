package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.PaymentResult

interface PaymentRepository {
    suspend fun getPayment(transferId: Long, offerId: Long?, gatewayId: String, percentage: Int): PaymentResult
}
