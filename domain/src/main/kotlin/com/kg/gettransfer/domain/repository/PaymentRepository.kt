package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.OfferItem
import com.kg.gettransfer.domain.model.PaymentRequest
import com.kg.gettransfer.domain.model.PlatronPayment
import com.kg.gettransfer.domain.model.CheckoutcomPayment
import com.kg.gettransfer.domain.model.BraintreePayment
import com.kg.gettransfer.domain.model.GooglePayPayment
import com.kg.gettransfer.domain.model.PaymentProcessRequest
import com.kg.gettransfer.domain.model.PaymentProcess
import com.kg.gettransfer.domain.model.PaymentStatusRequest
import com.kg.gettransfer.domain.model.PaymentStatus
import com.kg.gettransfer.domain.model.BraintreeToken
import com.kg.gettransfer.domain.model.Result

interface PaymentRepository {
    var selectedTransfer: Transfer?
    var selectedOffer: OfferItem?

    suspend fun getPlatronPayment(paymentRequest: PaymentRequest): Result<PlatronPayment>

    suspend fun getCheckoutcomPayment(paymentRequest: PaymentRequest): Result<CheckoutcomPayment>

    suspend fun getBraintreePayment(paymentRequest: PaymentRequest): Result<BraintreePayment>

    suspend fun getGooglePayPayment(paymentRequest: PaymentRequest): Result<GooglePayPayment>

    suspend fun getGroundPayment(paymentRequest: PaymentRequest): Result<Unit>

    suspend fun processPayment(paymentProcessRequest: PaymentProcessRequest): Result<PaymentProcess>

    suspend fun changeStatusPayment(paymentStatusRequest: PaymentStatusRequest): Result<PaymentStatus>

    suspend fun getBrainTreeToken(): Result<BraintreeToken>

    suspend fun confirmPaypal(paymentId: Long, nonce: String): Result<PaymentStatus>
}
