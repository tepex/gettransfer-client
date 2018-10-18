package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.PaymentStatusRequestEntity

import com.kg.gettransfer.domain.model.PaymentStatusRequest

/**
 * Map a [PaymentStatusRequestEntity] to and from a [PaymentStatusRequest] instance when data is moving between this later and the Domain layer.
 */
open class PaymentStatusRequestMapper(): Mapper<PaymentStatusRequestEntity, PaymentStatusRequest> {
    override fun fromEntity(type: PaymentStatusRequestEntity): PaymentStatusRequest { throw UnsupportedOperationException() }
    /**
     * Map a [PaymentStatusRequest] instance to a [PaymentStatusRequestEntity] instance.
     */
    override fun toEntity(type: PaymentStatusRequest) =
        PaymentStatusRequestEntity(type.paymentId, type.pgOrderId, type.withoutRedirect, type.success)
}
