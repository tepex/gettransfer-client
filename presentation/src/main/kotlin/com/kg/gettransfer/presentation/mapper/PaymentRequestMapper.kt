package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.PaymentRequest
import com.kg.gettransfer.presentation.model.PaymentRequestModel

open class PaymentRequestMapper : Mapper<PaymentRequestModel, PaymentRequest> {
    override fun fromView(type: PaymentRequestModel) =
        PaymentRequest(
            type.transferId,
            type.offerId,
            type.gatewayId,
            type.percentage,
            type.bookNowTransportType
        )

    override fun toView(type: PaymentRequest): PaymentRequestModel { throw UnsupportedOperationException() }
}
