package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.BraintreeTokenEntity
import com.kg.gettransfer.data.model.PaymentEntity
import com.kg.gettransfer.data.model.PaymentRequestEntity
import com.kg.gettransfer.data.model.PaymentStatusEntity
import com.kg.gettransfer.data.model.PaymentStatusRequestEntity
import com.kg.gettransfer.data.model.GooglePayPaymentEntity
import com.kg.gettransfer.data.model.GooglePayPaymentProcessEntity

import org.koin.core.KoinComponent

interface PaymentRemote : KoinComponent {

    suspend fun createPayment(paymentRequest: PaymentRequestEntity): PaymentEntity

    suspend fun createGooglePayPayment(paymentRequest: PaymentRequestEntity): GooglePayPaymentEntity

    suspend fun processGooglePayPayment(paymentProcess: GooglePayPaymentProcessEntity): PaymentEntity

    suspend fun changeStatusPayment(paymentStatusRequest: PaymentStatusRequestEntity): PaymentStatusEntity

    suspend fun getBraintreeToken(): BraintreeTokenEntity

    suspend fun confirmPaypal(paymentId: Long, nonce: String): PaymentStatusEntity
}
