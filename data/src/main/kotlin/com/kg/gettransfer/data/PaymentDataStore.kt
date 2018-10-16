package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.PaymentEntity

interface PaymentDataStore {
    suspend fun createPayment(transferId: Long, offerId: Long?, gatewayId: String, percentage: Int): PaymentEntity
}
