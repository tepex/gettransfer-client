package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.PaymentProcessRequest
import com.kg.gettransfer.presentation.model.PaymentProcessRequestModel

open class PaymentProcessRequestMapper : Mapper<PaymentProcessRequestModel, PaymentProcessRequest> {
    override fun fromView(type: PaymentProcessRequestModel) =
        PaymentProcessRequest(
            type.paymentId,
            type.token,
            type.isStringToken
        )

    override fun toView(type: PaymentProcessRequest): PaymentProcessRequestModel {
        throw UnsupportedOperationException()
    }
}