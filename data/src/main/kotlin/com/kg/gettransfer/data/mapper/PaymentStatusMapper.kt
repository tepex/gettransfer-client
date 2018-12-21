package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.PaymentStatusEntity

import com.kg.gettransfer.domain.model.PaymentStatus

/**
 * Map a [PaymentStatusEntity] to and from a [PaymentStatus] instance when data is moving between this later and the Domain layer.
 */
open class PaymentStatusMapper : Mapper<PaymentStatusEntity, PaymentStatus> {
    /**
     * Map a [PaymentStatusEntity] instance to a [PaymentStatus] instance.
     */
    override fun fromEntity(type: PaymentStatusEntity) =
        PaymentStatus(
            id = type.id,
            status = type.status
        )

    override fun toEntity(type: PaymentStatus): PaymentStatusEntity { throw UnsupportedOperationException() }
}
