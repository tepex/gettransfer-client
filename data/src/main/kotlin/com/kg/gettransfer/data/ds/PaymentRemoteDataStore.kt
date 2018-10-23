package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.PaymentRemote
import com.kg.gettransfer.data.PaymentDataStore
import com.kg.gettransfer.data.RemoteException

import com.kg.gettransfer.data.mapper.ExceptionMapper

import com.kg.gettransfer.data.model.PaymentEntity
import com.kg.gettransfer.data.model.PaymentRequestEntity
import com.kg.gettransfer.data.model.PaymentStatusEntity
import com.kg.gettransfer.data.model.PaymentStatusRequestEntity

/**
 * Implementation of the [RemoteDataStore] interface to provide a means of communicating with the remote data source.
 */
open class PaymentRemoteDataStore(private val remote: PaymentRemote): PaymentDataStore {
    override suspend fun createPayment(paymentRequest: PaymentRequestEntity): PaymentEntity {
        try { return remote.createPayment(paymentRequest) }
        catch(e: RemoteException) { throw ExceptionMapper.map(e) }
    }
    
    override suspend fun changeStatusPayment(paymentStatusRequest: PaymentStatusRequestEntity): PaymentStatusEntity {
        try { return remote.changeStatusPayment(paymentStatusRequest) }
        catch(e: RemoteException) { throw ExceptionMapper.map(e) }
    }
}
