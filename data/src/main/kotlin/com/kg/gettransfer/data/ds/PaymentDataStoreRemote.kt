package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.PaymentRemote
import com.kg.gettransfer.data.PaymentDataStore
import com.kg.gettransfer.data.model.BraintreeTokenEntity

import com.kg.gettransfer.data.model.PaymentRequestEntity
import com.kg.gettransfer.data.model.PaymentStatusRequestEntity

import org.koin.standalone.inject

/**
 * Implementation of the [RemoteDataStore] interface to provide a means of communicating with the remote data source.
 */
open class PaymentDataStoreRemote: PaymentDataStore {
    private val remote: PaymentRemote by inject()

    override suspend fun createPayment(paymentRequest: PaymentRequestEntity) = remote.createPayment(paymentRequest)

    override suspend fun changeStatusPayment(paymentStatusRequest: PaymentStatusRequestEntity) = remote.changeStatusPayment(paymentStatusRequest)

    override suspend fun getBraintreeToken(): BraintreeTokenEntity = remote.getBraintreeToken()

    override suspend fun confirmPaypal(paymentId: Long, nonce: String) = remote.confirmPaypal(paymentId, nonce)
}
