package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.PaymentRequestEntity

import com.kg.gettransfer.domain.model.PaymentRequest

/**
 * Map a [PaymentRequestEntity] to and from a [PaymentRequest] instance when data is moving between this later and the Domain layer.
 */
open class PaymentRequestMapper : Mapper<PaymentRequestEntity, PaymentRequest> {
    override fun fromEntity(type: PaymentRequestEntity): PaymentRequest { throw UnsupportedOperationException() }
    /**
     * Map a [PaymentRequest] instance to a [PaymentRequestEntity] instance.
     */
    override fun toEntity(type: PaymentRequest) =
        PaymentRequestEntity(
            transferId = type.transferId,
            offerId = type.offerId,
            gatewayId = type.gatewayId,
            percentage = type.percentage
        )
}
