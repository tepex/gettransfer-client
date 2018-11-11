package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.PaymentDataStore

import com.kg.gettransfer.data.ds.DataStoreFactory
import com.kg.gettransfer.data.ds.PaymentDataStoreCache
import com.kg.gettransfer.data.ds.PaymentDataStoreRemote

import com.kg.gettransfer.data.mapper.PaymentMapper
import com.kg.gettransfer.data.mapper.PaymentRequestMapper
import com.kg.gettransfer.data.mapper.PaymentStatusMapper
import com.kg.gettransfer.data.mapper.PaymentStatusRequestMapper

import com.kg.gettransfer.domain.model.PaymentRequest
import com.kg.gettransfer.domain.model.PaymentStatusRequest

import com.kg.gettransfer.domain.repository.PaymentRepository

class PaymentRepositoryImpl(private val factory: DataStoreFactory<PaymentDataStore, PaymentDataStoreCache, PaymentDataStoreRemote>,
                            private val paymentRequestMapper: PaymentRequestMapper,
                            private val paymentMapper: PaymentMapper,
                            private val paymentStatusRequestMapper: PaymentStatusRequestMapper,
                            private val paymentStatusMapper: PaymentStatusMapper): BaseRepository(), PaymentRepository {
    override suspend fun getPayment(paymentRequest: PaymentRequest) =
        paymentMapper.fromEntity(factory.retrieveRemoteDataStore().createPayment(paymentRequestMapper.toEntity(paymentRequest)))
    override suspend fun changeStatusPayment(paymentStatusRequest: PaymentStatusRequest) =
        paymentStatusMapper.fromEntity(factory.retrieveRemoteDataStore().changeStatusPayment(paymentStatusRequestMapper.toEntity(paymentStatusRequest)))
}
