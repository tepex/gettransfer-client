package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.OfferItem
import com.kg.gettransfer.domain.model.PaymentRequest
import com.kg.gettransfer.domain.model.Payment
import com.kg.gettransfer.domain.model.PaymentStatusRequest
import com.kg.gettransfer.domain.model.PaymentStatus
import com.kg.gettransfer.domain.model.BraintreeToken
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.GooglePayPayment
import com.kg.gettransfer.domain.model.GooglePayPaymentProcess

interface PaymentRepository {
    var selectedTransfer: Transfer?
    var selectedOffer: OfferItem?

    suspend fun getPayment(paymentRequest: PaymentRequest): Result<Payment>

    suspend fun getGooglePayPayment(paymentRequest: PaymentRequest): Result<GooglePayPayment>

    suspend fun processGooglePayPayment(paymentProcess: GooglePayPaymentProcess): Result<Payment>

    suspend fun changeStatusPayment(paymentStatusRequest: PaymentStatusRequest): Result<PaymentStatus>

    suspend fun getBrainTreeToken(): Result<BraintreeToken>

    suspend fun confirmPaypal(paymentId: Long, nonce: String): Result<PaymentStatus>
}
