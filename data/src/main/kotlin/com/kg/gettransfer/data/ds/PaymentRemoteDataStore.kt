package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.PaymentRemote
import com.kg.gettransfer.data.PaymentDataStore

import com.kg.gettransfer.data.model.PaymentRequestEntity

/**
 * Implementation of the [RemoteDataStore] interface to provide a means of communicating with the remote data source.
 */
open class PaymentRemoteDataStore(private val remote: PaymentRemote): PaymentDataStore {
    override suspend fun createPayment(paymentRequest: PaymentRequestEntity) = remote.createPayment(paymentRequest)
}
