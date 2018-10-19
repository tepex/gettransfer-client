package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.PaymentEntity

import com.kg.gettransfer.domain.model.Payment

/**
 * Map a [PaymentEntity] to and from a [Payment] instance when data is moving between this later and the Domain layer.
 */
open class PaymentMapper(): Mapper<PaymentEntity, Payment> {
    /**
     * Map a [PaymentEntity] instance to a [Payment] instance.
     */
    override fun fromEntity(type: PaymentEntity) = Payment(type.type, type.url, type.id)
    override fun toEntity(type: Payment): PaymentEntity { throw UnsupportedOperationException() }
}
