package com.kg.gettransfer.data

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

import org.koin.core.KoinComponent

interface PaymentDataStore : KoinComponent {

    suspend fun createPlatronPayment(paymentRequest: PaymentRequestEntity): PlatronPaymentEntity

    suspend fun createCheckoutcomPayment(paymentRequest: PaymentRequestEntity): CheckoutcomPaymentEntity

    suspend fun createBraintreePayment(paymentRequest: PaymentRequestEntity): BraintreePaymentEntity

    suspend fun createGooglePayPayment(paymentRequest: PaymentRequestEntity): GooglePayPaymentEntity

    suspend fun createGroundPayment(paymentRequest: PaymentRequestEntity)

    suspend fun processPayment(paymentProcessRequest: PaymentProcessRequestEntity): PaymentProcessEntity

    suspend fun changeStatusPayment(paymentStatusRequest: PaymentStatusRequestEntity): PaymentStatusEntity

    suspend fun getBraintreeToken(): BraintreeTokenEntity

    suspend fun confirmPaypal(paymentId: Long, nonce: String): PaymentStatusEntity
}
