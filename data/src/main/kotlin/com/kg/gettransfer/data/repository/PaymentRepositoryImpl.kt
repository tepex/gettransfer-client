package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.PaymentDataStore

import com.kg.gettransfer.data.ds.PaymentDataStoreFactory
import com.kg.gettransfer.data.ds.PaymentRemoteDataStore

import com.kg.gettransfer.data.mapper.PaymentMapper

import com.kg.gettransfer.domain.repository.PaymentRepository

class PaymentRepositoryImpl(private val factory: PaymentDataStoreFactory,
                            private val mapper: PaymentMapper): PaymentRepository {
    override suspend fun getPayment(transferId: Long, offerId: Long?, gatewayId: String, percentage: Int) =
        mapper.fromEntity(factory.retrieveRemoteDataStore().createPayment(transferId, offerId, gatewayId, percentage))
}
