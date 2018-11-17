package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.PaymentDataStore

import com.kg.gettransfer.data.ds.DataStoreFactory
import com.kg.gettransfer.data.ds.PaymentDataStoreCache
import com.kg.gettransfer.data.ds.PaymentDataStoreRemote

import com.kg.gettransfer.data.mapper.PaymentMapper
import com.kg.gettransfer.data.mapper.PaymentRequestMapper
import com.kg.gettransfer.data.mapper.PaymentStatusMapper
import com.kg.gettransfer.data.mapper.PaymentStatusRequestMapper

import com.kg.gettransfer.data.model.PaymentEntity
import com.kg.gettransfer.data.model.PaymentStatusEntity

import com.kg.gettransfer.domain.model.Payment
import com.kg.gettransfer.domain.model.PaymentRequest
import com.kg.gettransfer.domain.model.PaymentStatus
import com.kg.gettransfer.domain.model.PaymentStatusRequest
import com.kg.gettransfer.domain.model.Result

import com.kg.gettransfer.domain.repository.PaymentRepository

import org.koin.standalone.inject
import org.koin.standalone.KoinComponent

class PaymentRepositoryImpl(private val factory: DataStoreFactory<PaymentDataStore, PaymentDataStoreCache, PaymentDataStoreRemote>):
                                BaseRepository(), PaymentRepository, KoinComponent {
    private val paymentRequestMapper: PaymentRequestMapper by inject()
    private val paymentMapper: PaymentMapper by inject()
    private val paymentStatusRequestMapper: PaymentStatusRequestMapper by inject()
    private val paymentStatusMapper: PaymentStatusMapper by inject()

    override suspend fun getPayment(paymentRequest: PaymentRequest): Result<Payment> =
        retrieveRemoteModel<PaymentEntity, Payment>(paymentMapper, Payment("", null, null)) {
            factory.retrieveRemoteDataStore().createPayment(paymentRequestMapper.toEntity(paymentRequest))
        }

    override suspend fun changeStatusPayment(paymentStatusRequest: PaymentStatusRequest): Result<PaymentStatus> =
        retrieveRemoteModel<PaymentStatusEntity, PaymentStatus>(paymentStatusMapper, PaymentStatus(0, "")) {
            factory.retrieveRemoteDataStore().changeStatusPayment(paymentStatusRequestMapper.toEntity(paymentStatusRequest))
        }
}
