package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.PaymentRequest
import com.kg.gettransfer.domain.model.PaymentStatusRequest

import com.kg.gettransfer.domain.repository.PaymentRepository

class PaymentInteractor(private val repository: PaymentRepository) {
    suspend fun getPayment(paymentRequest: PaymentRequest) = repository.getPayment(paymentRequest)
    suspend fun changeStatusPayment(paymentStatusRequest: PaymentStatusRequest) =
        repository.changeStatusPayment(paymentStatusRequest)
    suspend fun getBrainTreeToken() = repository.getBrainTreeToken()
    suspend fun confirmPaypal(paymentId: Long, nonce: String) = repository.confirmPaypal(paymentId, nonce)
}
