package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.PaymentDataStore
import com.kg.gettransfer.data.model.*


/**
 * Implementation of the [PaymentDataStore] interface to provide a means of communicating with the cache data source.
 */
open class PaymentDataStoreCache: PaymentDataStore {
    
    override suspend fun createPayment(paymentRequest: PaymentRequestEntity): PaymentEntity {
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
