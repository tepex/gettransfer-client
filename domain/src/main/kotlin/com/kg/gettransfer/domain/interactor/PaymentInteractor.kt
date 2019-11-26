package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.eventListeners.PaymentStatusEventListener
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.OfferItem
import com.kg.gettransfer.domain.model.PaymentRequest
import com.kg.gettransfer.domain.model.PaymentStatusRequest
import com.kg.gettransfer.domain.model.PaymentProcessRequest

import com.kg.gettransfer.domain.repository.PaymentRepository

class PaymentInteractor(private val repository: PaymentRepository) {

    var eventPaymentReceiver: PaymentStatusEventListener? = null

    var selectedTransfer: Transfer?
        get() = repository.selectedTransfer
        set(value) { repository.selectedTransfer = value }

    var selectedOffer: OfferItem?
        get() = repository.selectedOffer
        set(value) { repository.selectedOffer = value }

    suspend fun getPlatronPayment(paymentRequest: PaymentRequest) = repository.getPlatronPayment(paymentRequest)

    suspend fun getCheckoutcomPayment(paymentRequest: PaymentRequest) = repository.getCheckoutPayment(paymentRequest)

    suspend fun getBraintreePayment(paymentRequest: PaymentRequest) = repository.getBraintreePayment(paymentRequest)

    suspend fun getGooglePayPayment(paymentRequest: PaymentRequest) = repository.getGooglePayPayment(paymentRequest)

    suspend fun getGroundPayment(paymentRequest: PaymentRequest) = repository.getGroundPayment(paymentRequest)

    suspend fun processPayment(paymentProcessRequest: PaymentProcessRequest) = repository.processPayment(paymentProcessRequest)

    suspend fun changeStatusPayment(paymentStatusRequest: PaymentStatusRequest) =
        repository.changeStatusPayment(paymentStatusRequest)

    suspend fun getBrainTreeToken() = repository.getBrainTreeToken()

    suspend fun confirmPaypal(paymentId: Long, nonce: String) = repository.confirmPaypal(paymentId, nonce)

    fun onNewPaymentStatusEvent(isSuccess: Boolean) { eventPaymentReceiver?.onNewPaymentStatusEvent(isSuccess) }
}
