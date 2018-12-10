package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.PaymentStatusRequest
import com.kg.gettransfer.presentation.model.PaymentStatusRequestModel

open class PaymentStatusRequestMapper : Mapper<PaymentStatusRequestModel, PaymentStatusRequest> {
    override fun fromView(type: PaymentStatusRequestModel) =
        PaymentStatusRequest(type.paymentId, type.pgOrderId, type.withoutRedirect, type.success)

    override fun toView(type: PaymentStatusRequest): PaymentStatusRequestModel { throw UnsupportedOperationException() }
}
