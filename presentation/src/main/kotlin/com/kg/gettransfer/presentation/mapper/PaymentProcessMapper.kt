package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.PaymentProcess
import com.kg.gettransfer.presentation.model.PaymentProcessModel

open class PaymentProcessMapper : Mapper<PaymentProcessModel, PaymentProcess> {
    override fun fromView(type: PaymentProcessModel) =
        PaymentProcess(
            type.paymentId,
            type.token,
            type.isStringToken
        )

    override fun toView(type: PaymentProcess): PaymentProcessModel {
        throw UnsupportedOperationException()
    }
}