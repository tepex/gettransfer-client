package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.PaymentRequest
import com.kg.gettransfer.domain.repository.PaymentRepository

class PaymentInteractor(private val repository: PaymentRepository) {
    suspend fun getPayment(paymentRequest: PaymentRequest) = repository.getPayment(paymentRequest)
}
