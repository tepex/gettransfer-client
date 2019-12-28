package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.PaymentDataStore
import com.kg.gettransfer.data.model.PaymentRequestEntity
import com.kg.gettransfer.data.model.PlatronPaymentEntity
import com.kg.gettransfer.data.model.CheckoutcomPaymentEntity
import com.kg.gettransfer.data.model.BraintreePaymentEntity
import com.kg.gettransfer.data.model.GooglePayPaymentEntity
import com.kg.gettransfer.data.model.PaymentProcessRequestEntity
import com.kg.gettransfer.data.model.PaymentProcessEntity
import com.kg.gettransfer.data.model.PaymentStatusRequestEntity
import com.kg.gettransfer.data.model.PaymentStatusEntity
import com.kg.gettransfer.data.model.BraintreeTokenEntity
import com.kg.gettransfer.data.model.CheckoutcomTokenRequestEntity
import com.kg.gettransfer.data.model.CheckoutcomTokenEntity

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

    override suspend fun getCheckoutcomToken(
        tokenRequest: CheckoutcomTokenRequestEntity,
        url: String,
        key: String
    ): CheckoutcomTokenEntity {
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
