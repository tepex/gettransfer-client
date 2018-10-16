package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.repository.PaymentRepository

class PaymentInteractor(private val repository: PaymentRepository) {
    suspend fun getPayment(transferId: Long, offerId: Long?, gatewayId: String, percentage: Int) = 
        repository.getPayment(transferId, offerId, gatewayId, percentage)
}
