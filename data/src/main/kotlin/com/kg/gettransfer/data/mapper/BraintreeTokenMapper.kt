package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.BraintreeTokenEntity
import com.kg.gettransfer.domain.model.BraintreeToken

open class BraintreeTokenMapper: Mapper<BraintreeTokenEntity, BraintreeToken> {
    override fun fromEntity(type: BraintreeTokenEntity): BraintreeToken =
            BraintreeToken(
                    token = type.token,
                    environment = type.environment)

    override fun toEntity(type: BraintreeToken): BraintreeTokenEntity {
        throw UnsupportedOperationException()
    }
}