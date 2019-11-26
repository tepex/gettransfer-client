package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.PaymentDataStore
import com.kg.gettransfer.data.model.*

/**
 * Implementation of the [PaymentDataStore] interface to provide a means of communicating with the cache data source.
 */
open class PaymentDataStoreCache : PaymentDataStore {

    override suspend fun createPlatronPayment(paymentRequest: PaymentRequestEntity): PlatronPaymentEntity {
        throw UnsupportedOperationException()
    }

    override suspend fun createCheckoutcomPayment(paymentRequest: PaymentRequestEntity): CheckoutcomPaymentEntity {
        throw UnsupportedOperationException()
    }

    override suspend fun createBraintreePayment(paymentRequest: PaymentRequestEntity): BraintreePaymentEntity {
        throw UnsupportedOperationException()
    }

    override suspend fun createGooglePayPayment(paymentRequest: PaymentRequestEntity): GooglePayPaymentEntity {
        throw UnsupportedOperationException()
    }

    override suspend fun createGroundPayment(paymentRequest: PaymentRequestEntity) {
        throw UnsupportedOperationException()
    }

    override suspend fun processPayment(paymentProcessRequest: PaymentProcessRequestEntity): PaymentProcessEntity {
        throw UnsupportedOperationException()
    }

    override suspend fun changeStatusPayment(paymentStatusRequest: PaymentStatusRequestEntity): PaymentStatusEntity {
        throw UnsupportedOperationException()
    }

    override suspend fun getBraintreeToken(): BraintreeTokenEntity {
        throw UnsupportedOperationException()
    }

    override suspend fun confirmPaypal(paymentId: Long, nonce: String): PaymentStatusEntity {
        throw UnsupportedOperationException()
    }
}
