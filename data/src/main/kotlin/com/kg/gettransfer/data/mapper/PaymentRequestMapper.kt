package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.PaymentRequestEntity

import com.kg.gettransfer.domain.model.PaymentRequest

/**
 * Map a [PaymentRequestEntity] to and from a [PaymentRequest] instance when data is moving between this later and the Domain layer.
 */
open class PaymentRequestMapper(): Mapper<PaymentRequestEntity, PaymentRequest> {
    /**
     * Map a [PaymentRequestEntity] instance to a [PaymentRequest] instance.
     */
    override fun fromEntity(type: PaymentRequestEntity) =
        PaymentRequest(type.transferId, type.offerId, type.gatewayId, type.percentage)
    /**
     * Map a [PaymentRequest] instance to a [PaymentRequestEntity] instance.
     */
    override fun toEntity(type: PaymentRequest) =
        PaymentRequestEntity(type.transferId, type.offerId, type.gatewayId, type.percentage)
}
