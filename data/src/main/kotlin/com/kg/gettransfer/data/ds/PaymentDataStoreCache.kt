package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.PaymentCache
import com.kg.gettransfer.data.PaymentDataStore
import com.kg.gettransfer.data.RemoteException

import com.kg.gettransfer.data.mapper.ExceptionMapper

import com.kg.gettransfer.data.model.PaymentEntity
import com.kg.gettransfer.data.model.PaymentRequestEntity
import com.kg.gettransfer.data.model.PaymentStatusEntity
import com.kg.gettransfer.data.model.PaymentStatusRequestEntity

/**
 * Implementation of the [PaymentDataStore] interface to provide a means of communicating with the cache data source.
 */
open class PaymentDataStoreCache(/* private val cache: PaymentCache*/): PaymentDataStore {
    override suspend fun createPayment(paymentRequest: PaymentRequestEntity): PaymentEntity {
        throw UnsupportedOperationException()
    }
    
    override suspend fun changeStatusPayment(paymentStatusRequest: PaymentStatusRequestEntity): PaymentStatusEntity {
        throw UnsupportedOperationException()
    }
}
