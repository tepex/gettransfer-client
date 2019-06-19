package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.PaymentEntity
import com.kg.gettransfer.data.model.map

import com.kg.gettransfer.domain.model.Payment

import org.koin.standalone.get

/**
 * Map a [PaymentEntity] to and from a [Payment] instance when data is moving between this later and the Domain layer.
 */
open class PaymentMapper : Mapper<PaymentEntity, Payment> {
    override fun fromEntity(type: PaymentEntity) =
        Payment(
            type = type.type,
            url = type.url,
            id = type.id,
            params = type.params?.let { it.map() }
        )

    override fun toEntity(type: Payment): PaymentEntity { throw UnsupportedOperationException() }
}
