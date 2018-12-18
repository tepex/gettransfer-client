package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.PaypalCredentialsEntity

import com.kg.gettransfer.domain.model.PaypalCredentials
/**
 * Map a [PaypalCredentialsEntity] to and from a [PaypalCredentials] instance when data is moving between this later and the Domain layer.
 */
open class PaypalCredentialsMapper : Mapper<PaypalCredentialsEntity, PaypalCredentials> {
    override fun fromEntity(type: PaypalCredentialsEntity) =
        PaypalCredentials(
            id = type.id,
            env = type.env
        )

    override fun toEntity(type: PaypalCredentials) =
        PaypalCredentialsEntity(
            id = type.id,
            env = type.env
        )
}
