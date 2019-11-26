package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.*

interface PaymentRepository {
    var selectedTransfer: Transfer?
    var selectedOffer: OfferItem?

    suspend fun getPlatronPayment(paymentRequest: PaymentRequest): Result<PlatronPayment>

    suspend fun getCheckoutPayment(paymentRequest: PaymentRequest): Result<CheckoutcomPayment>

    suspend fun getBraintreePayment(paymentRequest: PaymentRequest): Result<BraintreePayment>

    suspend fun getGooglePayPayment(paymentRequest: PaymentRequest): Result<GooglePayPayment>

    suspend fun getGroundPayment(paymentRequest: PaymentRequest): Result<Unit>

    suspend fun processPayment(paymentProcessRequest: PaymentProcessRequest): Result<PaymentProcess>

    suspend fun changeStatusPayment(paymentStatusRequest: PaymentStatusRequest): Result<PaymentStatus>

    suspend fun getBrainTreeToken(): Result<BraintreeToken>

    suspend fun confirmPaypal(paymentId: Long, nonce: String): Result<PaymentStatus>
}
