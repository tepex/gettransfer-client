package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.PaymentDataStore
import com.kg.gettransfer.data.PaymentRemote
import com.kg.gettransfer.data.model.PaymentRequestEntity
import com.kg.gettransfer.data.model.PaymentProcessRequestEntity
import com.kg.gettransfer.data.model.PaymentStatusRequestEntity
import com.kg.gettransfer.data.model.BraintreeTokenEntity
import com.kg.gettransfer.data.model.CheckoutcomTokenRequestEntity
import org.koin.core.inject

/**
 * Implementation of the [PaymentDataStore] interface to provide a means of communicating with the remote data source.
 */
open class PaymentDataStoreRemote : PaymentDataStore {

    private val remote: PaymentRemote by inject()

    override suspend fun createPlatronPayment(paymentRequest: PaymentRequestEntity) =
        remote.createPlatronPayment(paymentRequest)

    override suspend fun createCheckoutcomPayment(paymentRequest: PaymentRequestEntity) =
        remote.createCheckoutcomPayment(paymentRequest)

    override suspend fun getCheckoutcomToken(tokenRequest: CheckoutcomTokenRequestEntity, url: String, key: String) =
        remote.getCheckoutcomToken(tokenRequest, url, key)

    override suspend fun createBraintreePayment(paymentRequest: PaymentRequestEntity) =
        remote.createBraintreePayment(paymentRequest)

    override suspend fun createGooglePayPayment(paymentRequest: PaymentRequestEntity) =
        remote.createGooglePayPayment(paymentRequest)

    override suspend fun createGroundPayment(paymentRequest: PaymentRequestEntity) =
        remote.createGroundPayment(paymentRequest)

    override suspend fun processPayment(paymentProcessRequest: PaymentProcessRequestEntity) =
        remote.processPayment(paymentProcessRequest)

    override suspend fun changeStatusPayment(paymentStatusRequest: PaymentStatusRequestEntity) =
        remote.changeStatusPayment(paymentStatusRequest)

    override suspend fun getBraintreeToken(): BraintreeTokenEntity = remote.getBraintreeToken()

    override suspend fun confirmPaypal(paymentId: Long, nonce: String) =
        remote.confirmPaypal(paymentId, nonce)
}
