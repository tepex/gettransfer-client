package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.PaymentDataStore

import com.kg.gettransfer.data.ds.PaymentDataStoreFactory
import com.kg.gettransfer.data.ds.PaymentRemoteDataStore

import com.kg.gettransfer.data.mapper.PaymentMapper
import com.kg.gettransfer.data.mapper.PaymentRequestMapper

import com.kg.gettransfer.domain.model.PaymentRequest
import com.kg.gettransfer.domain.repository.PaymentRepository

class PaymentRepositoryImpl(private val factory: PaymentDataStoreFactory,
                            private val paymentRequestMapper: PaymentRequestMapper,
                            private val paymentMapper: PaymentMapper): PaymentRepository {
    override suspend fun getPayment(paymentRequest: PaymentRequest) =
        paymentMapper.fromEntity(factory.retrieveRemoteDataStore().createPayment(paymentRequestMapper.toEntity(paymentRequest)))
    override suspend fun changeStatusPayment(paymentId: Long, pgOrderId: Long, withoutRedirect: Boolean, status: String) =
            factory.changeStatusPayment(paymentId, pgOrderId, withoutRedirect, status)
}
