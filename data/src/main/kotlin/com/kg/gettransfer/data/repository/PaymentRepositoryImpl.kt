package com.kg.gettransfer.data.repository

import com.kg.gettransfer.domain.repository.PaymentRepository

class PaymentRepositoryImpl(private val apiRepository: ApiRepositoryImpl): PaymentRepository {
    override suspend fun getPayment(transferId: Long,
                                    offerId: Long?,
                                    gatewayId: String,
                                    percentage: Int) = apiRepository.createPayment(transferId, offerId, gatewayId, percentage)
}
